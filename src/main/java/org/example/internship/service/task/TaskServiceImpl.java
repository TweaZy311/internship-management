package org.example.internship.service.task;

import lombok.RequiredArgsConstructor;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.model.response.task.Task;
import org.example.internship.entity.LessonEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.entity.TaskEntity;
import org.example.internship.entity.UserRole;
import org.example.internship.entity.UserEntity;
import org.example.internship.repository.LessonRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.gitlab4j.api.models.Project;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
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
    public Task save(CreateTaskRequest createTaskRequest) {
        TaskEntity taskEntity = mapper.map(createTaskRequest, TaskEntity.class);
        taskEntity.setLesson(lessonRepository.findById(createTaskRequest.getLessonId()).orElseThrow(
                () -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), "Lesson with such ID has not been found")
        ));

        Project project = gitlabService.createRepository(createTaskRequest.getName(), createTaskRequest.getDescription());
        String url = project.getWebUrl();
        Long projectId = project.getId();

        taskEntity.setRepository(url);
        taskEntity.setRepositoryId(projectId);
        taskEntity = taskRepository.save(taskEntity);
        return mapper.map(taskEntity, Task.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех опубликованных заданий
     */
    @Override
    public List<Task> getAllPublished() {
        List<TaskEntity> tasks = taskRepository.findAllByPublishDateLessThanEqual(LocalDate.now());
        return mapper.mapAsList(tasks, Task.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор задания
     * @return информация о задании
     * @throws ServiceException если задание не найдено
     */
    @Override
    public Task getById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return mapper.map(task, Task.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param updateTaskRequest данные обновленного задания
     * @throws ServiceException если задание не найдено
     */
    @Override
    public Task update(UpdateTaskRequest updateTaskRequest) {
        TaskEntity existingTask = taskRepository.findById(updateTaskRequest.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        //todo
        mapper.map(updateTaskRequest, existingTask);
        existingTask = taskRepository.save(existingTask);
        return mapper.map(existingTask, Task.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заданий
     */
    @Override
    public List<Task> getAll() {
        List<TaskEntity> tasks = taskRepository.findAll();
        return mapper.mapAsList(tasks, Task.class);
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
    public Task publishById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        LessonEntity lesson = task.getLesson();
        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }
        if (task.getPublishDate() != null && task.getPublishDate().isBefore(LocalDate.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), "Task with such ID is already published");
        }

        task.setPublishDate(LocalDate.now());
        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), UserRole.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        for (UserEntity user : users) {
            gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
        }
        task = taskRepository.save(task);
        return mapper.map(task, Task.class);
    }

    @Override
    public List<Task> publishByLessonId(Long lessonId) {
        List<TaskEntity> tasks = taskRepository.findAllByLessonIdAndPublishDateIsNull(lessonId);

        if (tasks.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), "Tasks not found for lesson with such ID");
        }
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.LSN_404.getCode(), "Lesson with such ID has not been found"));

        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }

        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), UserRole.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        List<TaskEntity> publishedTasks = new ArrayList<>();
        for (TaskEntity task : tasks) {
            task.setPublishDate(LocalDate.now());
            publishedTasks.add(taskRepository.save(task));
            for (UserEntity user : users) {
                gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
            }
        }
        return mapper.mapAsList(publishedTasks, Task.class);
    }
}
