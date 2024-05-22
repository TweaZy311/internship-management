package org.example.internship.service.task;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.exception.AlreadyPublishedException;
import org.example.internship.exception.NotPublishedException;
import org.example.internship.mapper.TaskMapper;
import org.example.internship.model.Lesson;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.gitlab4j.api.models.Project;
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
     * @throws EntityNotFoundException если задание не найдено
     */
    @Override
    public TaskDto getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    /**
     * {@inheritDoc}
     *
     * @param taskDto данные обновленного задания
     * @throws EntityNotFoundException если задание не найдено
     */
    @Override
    public void update(UpdateTaskDto taskDto) {
        Task existingTask = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskDto.getId()));

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
     * @throws EntityNotFoundException   если задание не найдено
     * @throws NotPublishedException     если занятие, к которому относится
     *                                   это задание еще не опубликовано
     * @throws AlreadyPublishedException если задание уже было ранее опубликовано
     */
    @Override
    public void publishById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        Lesson lesson = task.getLesson();
        if (!lesson.getIsPublished()) {
            throw new NotPublishedException("Lesson with this task is not published");
        }
        if (task.getPublishDate() != null && task.getPublishDate().isBefore(LocalDate.now())) {
            throw new AlreadyPublishedException("This task is already published");
        }

        task.setPublishDate(LocalDate.now());
        List<User> users = userRepository.findAllByInternshipIdAndRole(task.getLesson().getInternship().getId(), Role.USER);
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
            throw new EntityNotFoundException("Tasks not found for lesson with ID " + lessonId);
        }
        Lesson lesson = tasks.get(0).getLesson();
        if (!lesson.getIsPublished()) {
            throw new NotPublishedException("Lesson with these tasks is not published");
        }

        List<User> users = userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), Role.USER);
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
