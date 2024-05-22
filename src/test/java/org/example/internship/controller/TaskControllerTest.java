package org.example.internship.controller;

import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void createTask_returnCreated() {
        NewTaskDto newTaskDto = new NewTaskDto();
        newTaskDto.setName("Test Task");

        ResponseEntity<Void> response = taskController.createTask(newTaskDto);

        verify(taskService, times(1)).save(newTaskDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAllPublishedTasks_returnListOfTasks() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        when(taskService.getAllPublished()).thenReturn(List.of(taskDto));

        ResponseEntity<List<TaskDto>> response = taskController.getAllPublishedTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(taskDto, response.getBody().get(0));
    }

    @Test
    void getAllPublishedTasks_emptyList_returnNoContent() {
        when(taskService.getAllPublished()).thenReturn(List.of());

        ResponseEntity<List<TaskDto>> response = taskController.getAllPublishedTasks();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void publishTaskById_returnOk() {
        ResponseEntity<Void> response = taskController.publishTaskById(1L);

        verify(taskService, times(1)).publishById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void publishTasksByLessonId_returnOk() {
        ResponseEntity<Void> response = taskController.publishTasksByLessonId(1L);

        verify(taskService, times(1)).publishByLessonId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllTasks_returnListOfTasks() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        when(taskService.getAll()).thenReturn(List.of(taskDto));

        ResponseEntity<List<TaskDto>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(taskDto, response.getBody().get(0));
    }

    @Test
    void getAllTasks_emptyList_returnNoContent() {
        when(taskService.getAll()).thenReturn(List.of());

        ResponseEntity<List<TaskDto>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateTask_returnOk() {
        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setId(1L);
        updateTaskDto.setName("Updated Task");

        ResponseEntity<Void> response = taskController.updateTask(updateTaskDto);

        verify(taskService, times(1)).update(updateTaskDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTaskById_returnOk() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        when(taskService.getById(1L)).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
    }
}