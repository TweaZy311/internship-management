package org.example.internship.service.task;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.TaskMapper;
import org.example.internship.model.Lesson;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
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
//TODO ЗАМЕНИТЬ exception и javadoc
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
    public void save(NewTaskDto taskDto) {
        Task task = taskMapper.newDtoToModel(taskDto);

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
    public List<TaskDto> getAllPublished() {
        List<Task> tasks = taskRepository.findAllByPublishDateLessThanEqual(LocalDate.now());
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
    public TaskDto getById(Long id) {
        Task task = taskRepository.findById(id)
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
    public void update(UpdateTaskDto taskDto) {
        Task existingTask = taskRepository.findById(taskDto.getId())
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
    public List<TaskDto> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::modelToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор задания
     * @throws ServiceException   если задание не найдено
     * @throws ServiceException     если занятие, к которому относится
     *                                   это задание еще не опубликовано
     * @throws ServiceException если задание уже было ранее опубликовано
     */
    @Override
    public void publishById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), TASK_WITH_SUCH_ID_COULD_NOT_BE_FOUND));

        Lesson lesson = task.getLesson();
        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }
        if (task.getPublishDate() != null && task.getPublishDate().isBefore(LocalDate.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), "Task with such ID is already published");
        }

        task.setPublishDate(LocalDate.now());
        List<User> users = userRepository.findAllByInternshipIdAndRole(task.getLesson().getInternship().getId(), Role.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        for (User user : users) {
            gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
        }
        taskRepository.saveAndFlush(task);
    }

    @Override
    public void publishByLessonId(Long lessonId) {
        List<Task> tasks = taskRepository.findAllByLessonIdAndPublishDateIsNull(lessonId);

        if (tasks.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.TSK_404.getCode(), "Tasks not found for lesson with such ID");
        }
        Lesson lesson = tasks.get(0).getLesson();
        if (!lesson.getIsPublished()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.TSK_400.getCode(), LESSON_WITH_TASKS_NOT_PUBLISHED_YET);
        }

        List<User> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), Role.USER);
        //todo возможно стоит убрать
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        for (Task task : tasks) {
            task.setPublishDate(LocalDate.now());
            taskRepository.saveAndFlush(task);
            for (User user : users) {
                gitlabService.forkRepository(task.getRepositoryId(), user.getUsername());
            }
        }

    }
}
