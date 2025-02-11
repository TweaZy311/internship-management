package org.example.internship.service.task;

import lombok.RequiredArgsConstructor;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.model.response.task.Task;
import org.example.internship.entity.LessonEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.TaskMapper;
import org.example.internship.entity.task.TaskEntity;
import org.example.internship.entity.user.Role;
import org.example.internship.entity.user.UserEntity;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.gitlab4j.api.models.Project;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TaskMapper taskMapper;

    /**
     * {@inheritDoc}
     *
     * @param taskDto данные нового задания
     */
    @Override
    public void save(CreateTaskRequest taskDto) {
        TaskEntity task = taskMapper.newDtoToModel(taskDto);

        Project project = gitlabService.createRepository(taskDto.getName(), taskDto.getDescription());
        String url = project.getWebUrl();
        Long projectId = project.getId();

        task.setRepository(url);
        task.setRepositoryId(projectId);
        taskRepository.saveAndFlush(task);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех опубликованных заданий
     */
    @Override
    public List<Task> getAllPublished() {
        List<TaskEntity> tasks = taskRepository.findAllByPublishDateLessThanEqual(LocalDate.now());
        return tasks.stream()
                .map(taskMapper::modelToDto)
                .collect(Collectors.toList());
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
        return taskMapper.modelToDto(task);
    }

    /**
     * {@inheritDoc}
     *
     * @param taskDto данные обновленного задания
     * @throws ServiceException если задание не найдено
     */
    @Override
    public void update(UpdateTaskRequest taskDto) {
        TaskEntity existingTask = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        taskMapper.updateDtoToModel(existingTask, taskDto);
        taskRepository.saveAndFlush(existingTask);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заданий
     */
    @Override
    public List<Task> getAll() {
        List<TaskEntity> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::modelToDto)
                .collect(Collectors.toList());
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
    public void publishById(Long id) {
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
        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(task.getLesson().getInternship().getId(), Role.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        for (UserEntity user : users) {
            gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
        }
        taskRepository.saveAndFlush(task);
    }

    @Override
    public void publishByLessonId(Long lessonId) {
        List<TaskEntity> tasks = taskRepository.findAllByLessonIdAndPublishDateIsNull(lessonId);

        if (tasks.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), "Tasks not found for lesson with such ID");
        }
        LessonEntity lesson = tasks.get(0).getLesson();
        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }

        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), Role.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        for (TaskEntity task : tasks) {
            task.setPublishDate(LocalDate.now());
            taskRepository.saveAndFlush(task);
            for (UserEntity user : users) {
                gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
            }
        }

    }
}
