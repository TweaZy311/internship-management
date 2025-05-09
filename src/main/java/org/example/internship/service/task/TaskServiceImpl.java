package org.example.internship.service.task;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.*;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.repository.LessonRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.utils.SpecificationsBuilder;
import org.gitlab4j.api.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса для работы с заданиями.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final String TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Task with such ID could not be found";
    private final String LESSON_WITH_TASKS_NOT_PUBLISHED_YET = "Lesson with ID which contains these tasks is not published yet";

    private final TaskRepository taskRepository;
    private final GitlabService gitlabService;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param createTaskRequest данные нового задания
     */
    @Override
    public TaskEntity saveTask(CreateTaskRequest createTaskRequest) {
        TaskEntity taskEntity = mapper.map(createTaskRequest, TaskEntity.class);
        taskEntity.setLesson(lessonRepository.findById(createTaskRequest.getLessonId()).orElseThrow(
                () -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), "Lesson with such ID has not been found")
        ));

        Project project = gitlabService.createRepository(createTaskRequest.getRepositoryName(), createTaskRequest.getDescription());
        String url = project.getWebUrl();
        Long projectId = project.getId();

        taskEntity.setRepository(url);
        taskEntity.setRepositoryId(projectId);
        return taskRepository.save(taskEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех опубликованных заданий
     */
    @Override
    public List<TaskEntity> getAllPublished() {
        return taskRepository.findAllByIsPublished(true);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор задания
     * @return информация о задании
     * @throws ServiceException если задание не найдено
     */
    @Override
    public TaskEntity getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
    }

    /**
     * {@inheritDoc}
     *
     * @param updateTaskRequest данные обновленного задания
     * @throws ServiceException если задание не найдено
     */
    @Override
    public TaskEntity updateTask(UpdateTaskRequest updateTaskRequest) {
        TaskEntity existingTask = taskRepository.findById(updateTaskRequest.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        //todo
        mapper.map(updateTaskRequest, existingTask);
        return taskRepository.save(existingTask);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заданий
     */
    @Override
    public Page<TaskEntity> getTasks(BaseGetListRequest request) {
        SpecificationsBuilder<TaskEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return taskRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор задания
     * @throws ServiceException если задание не найдено
     * @throws ServiceException если занятие, к которому относится
     *                          это задание еще не опубликовано
     * @throws ServiceException если задание уже было ранее опубликовано
     */
    @Override
    public TaskEntity publishById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        LessonEntity lesson = task.getLesson();
        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }
        if (!task.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), "Task with such ID is already published");
        }

        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), UserRole.USER);

        for (UserEntity user : users) {
            gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
        }
        return taskRepository.save(task);
    }

    @Override
    public List<TaskEntity> publishByLessonId(Long lessonId) {
        List<TaskEntity> tasks = taskRepository.findAllByLessonIdAndIsPublished(lessonId, true);

        if (tasks.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), "Tasks not found for lesson with such ID");
        }
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), "Lesson with such ID has not been found"));

        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }

        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), UserRole.USER);

        List<TaskEntity> publishedTasks = new ArrayList<>();
        for (TaskEntity task : tasks) {
            task.setIsPublished(Boolean.TRUE);
            publishedTasks.add(taskRepository.save(task));
            for (UserEntity user : users) {
                gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
            }
        }
        return publishedTasks;
    }
}
