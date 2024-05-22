package org.example.internship.controller;

import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;
import org.example.internship.service.lesson.LessonService;
import org.example.internship.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private LessonController lessonController;

    private NewLessonDto newLessonDto;

    @BeforeEach
    void setUp() {
        newLessonDto = new NewLessonDto();
        newLessonDto.setName("Test Lesson");
        newLessonDto.setDescription("Test Description");
        newLessonDto.setInternshipId(1L);
    }

    @Test
    void createLesson_returnCreated(){
        ResponseEntity<Void> response = lessonController.createLesson(newLessonDto);

        verify(lessonService, times(1)).save(newLessonDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getLessonById_returnLessonDto() {
        UserLessonDto lessonDto = new UserLessonDto();
        lessonDto.setId(1L);

        when(lessonService.getById(1L)).thenReturn(lessonDto);

        ResponseEntity<UserLessonDto> response = lessonController.getLessonById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lessonDto, response.getBody());
    }

    @Test
    void getAllLessons_returnListOfLessons() {
        List<AdminLessonDto> lessons = List.of(new AdminLessonDto());

        when(lessonService.getAll()).thenReturn(lessons);

        ResponseEntity<List<AdminLessonDto>> response = lessonController.getAllLessons();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lessons, response.getBody());
    }

    @Test
    void getAllLessons_emptyList_returnNoContent() {
        when(lessonService.getAll()).thenReturn(List.of());

        ResponseEntity<List<AdminLessonDto>> response = lessonController.getAllLessons();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getPublishedLessons_returnListOfLessons() {
        List<UserLessonDto> lessons = List.of(new UserLessonDto());

        when(lessonService.getAllPublishedByInternshipId(1L)).thenReturn(lessons);

        ResponseEntity<List<UserLessonDto>> response = lessonController.getPublishedLessons(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lessons, response.getBody());
    }

    @Test
    void getPublishedLessons_emptyList_returnNoContent() {
        when(lessonService.getAllPublishedByInternshipId(1L)).thenReturn(List.of());

        ResponseEntity<List<UserLessonDto>> response = lessonController.getPublishedLessons(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void publishLesson_returnOk() {
        ResponseEntity<Void> response = lessonController.publishLesson(1L);

        verify(taskService, times(1)).publishByLessonId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}