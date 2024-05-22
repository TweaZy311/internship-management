package org.example.internship.service.impl;

import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.exception.AlreadyPublishedException;
import org.example.internship.exception.NotPublishedException;
import org.example.internship.mapper.TaskMapper;
import org.example.internship.model.Lesson;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.task.TaskServiceImpl;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GitlabService gitlabService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private NewTaskDto newTaskDto;
    private Task task;
    private TaskDto taskDto;
    private Lesson lesson;
    private Project project;
    private UpdateTaskDto updatedTaskDto;

    @BeforeEach
    void setUp() {
        Internship internship = Internship.builder()
                .id(1L)
                .name("Test Internship")
                .build();

        lesson = Lesson.builder()
                .id(1L)
                .isPublished(false)
                .internship(internship)
                .build();

        newTaskDto = new NewTaskDto();
        newTaskDto.setName("Test Task");
        newTaskDto.setDescription("Test Description");
        newTaskDto.setLessonId(1L);

        task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .lesson(lesson)
                .publishDate(null)
                .build();

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setLessonId(1L);

        updatedTaskDto = new UpdateTaskDto();
        updatedTaskDto.setId(1L);
        updatedTaskDto.setName("Updated Task");
        updatedTaskDto.setDescription("Updated Description");

        project = new Project();
        project.setId(2L);
        project.setWebUrl("https://example.com/project");
    }

    @Test
    void save_saveNewTask() {
        when(taskMapper.newDtoToModel(newTaskDto)).thenReturn(task);
        when(gitlabService.createRepository(task.getName(), task.getDescription())).thenReturn(project);

        taskService.save(newTaskDto);

        assertEquals("https://example.com/project", task.getRepository());
        assertEquals(2L, task.getRepositoryId());
        verify(taskRepository, times(1)).saveAndFlush(task);
    }

    @Test
    void getAllPublished_returnListOfPublishedTasks() {
        when(taskRepository.findAllByPublishDateLessThanEqual(LocalDate.now())).thenReturn(List.of(task));
        when(taskMapper.modelToDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getAllPublished();

        assertEquals(1, result.size());
        assertEquals(taskDto, result.get(0));
    }

    @Test
    void getById_returnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.modelToDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getById(1L);

        assertEquals(taskDto, result);
    }

    @Test
    void getById_taskNotFound_throwException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getById(1L));
    }

    @Test
    void update_updateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.update(updatedTaskDto);

        verify(taskMapper, times(1)).updateDtoToModel(task, updatedTaskDto);
        verify(taskRepository, times(1)).saveAndFlush(task);
    }

    @Test
    void update_taskNotFound_throwException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.update(updatedTaskDto));
    }


    @Test
    void getAll_returnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.modelToDto(any(Task.class))).thenReturn(taskDto);

        List<TaskDto> result = taskService.getAll();

        verify(taskMapper, times(1)).modelToDto(task);
        assertEquals(1, result.size());
    }

    @Test
    void publishById_publishTaskAndForkRepository() {
        List<User> users = List.of(User.builder().id(1L).username("user1").build());
        lesson.setIsPublished(true);
        task.setLesson(lesson);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findAllByInternshipIdAndRole(task.getLesson().getInternship().getId(),  Role.USER)).thenReturn(users);

        taskService.publishById(1L);

        verify(gitlabService, times(1)).forkRepository(task.getRepositoryId(), "user1");
        verify(taskRepository, times(1)).saveAndFlush(task);
        assertEquals(LocalDate.now(), task.getPublishDate());
    }

    @Test
    void publishById_taskNotFound_throwException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.publishById(1L));
    }

    @Test
    void publishById_lessonIsNotPublished_throwException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(NotPublishedException.class, () -> taskService.publishById(1L));
    }

    @Test
    void publishById_taskAlreadyPublished_throwException() {
        lesson.setIsPublished(true);
        task.setPublishDate(LocalDate.now().minusDays(1));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(AlreadyPublishedException.class, () -> taskService.publishById(1L));
    }

    @Test
    void publishByLessonId_publishTasksAndForkRepository() {
        List<User> users = List.of(User.builder().id(1L).username("user1").role(Role.USER).build());
        lesson.setIsPublished(true);
        task.setLesson(lesson);
        List<Task> tasks = List.of(task);

        when(taskRepository.findAllByLessonIdAndPublishDateIsNull(1L)).thenReturn(tasks);
        when(userRepository.findAllByInternshipIdAndRole(lesson.getInternship().getId(), Role.USER)).thenReturn(users);

        taskService.publishByLessonId(1L);

        for (Task task : tasks) {
            assertEquals(LocalDate.now(), task.getPublishDate());
            verify(taskRepository, times(1)).saveAndFlush(task);
        }

        verify(gitlabService, times(1)).forkRepository(task.getRepositoryId(), "user1");
    }

    @Test
    void publishByLessonId_lessonIsNotPublished_throwException() {
        when(taskRepository.findAllByLessonIdAndPublishDateIsNull(1L)).thenReturn(List.of(task));

        assertThrows(NotPublishedException.class, () -> taskService.publishByLessonId(1L));
    }
}