package org.example.internship.service.impl;

import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;
import org.example.internship.exception.AlreadyPublishedException;
import org.example.internship.mapper.LessonMapper;
import org.example.internship.model.Lesson;
import org.example.internship.model.internship.Internship;
import org.example.internship.repository.LessonRepository;
import org.example.internship.service.lesson.LessonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonServiceImpl lessonService;

    private NewLessonDto newLessonDto;
    private Lesson lesson;
    private AdminLessonDto adminLessonDto;
    private UserLessonDto userLessonDto;

    @BeforeEach
    void setUp() {
        newLessonDto = new NewLessonDto();
        newLessonDto.setName("Test Lesson");
        newLessonDto.setDescription("Test Description");
        newLessonDto.setInternshipId(1L);

        Internship internship = Internship.builder().id(1L).build();

        lesson = Lesson.builder()
                .id(1L)
                .name("Test Lesson")
                .description("Test Description")
                .isPublished(false)
                .internship(internship)
                .build();

        adminLessonDto = new AdminLessonDto();
        adminLessonDto.setId(1L);
        adminLessonDto.setName("Test Lesson");
        adminLessonDto.setDescription("Test Description");
        adminLessonDto.setIsPublished(false);
        adminLessonDto.setInternshipId(1L);

        userLessonDto = new UserLessonDto();
        userLessonDto.setId(1L);
        userLessonDto.setName("Test Lesson");
        userLessonDto.setDescription("Test Description");
        userLessonDto.setInternshipId(1L);
    }

    @Test
    void save_saveNewLesson() {
        when(lessonMapper.newLessonDtoToModel(newLessonDto)).thenReturn(lesson);

        lessonService.save(newLessonDto);

        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    void getById_returnLesson() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonMapper.modelToUserDto(lesson)).thenReturn(userLessonDto);

        UserLessonDto result = lessonService.getById(1L);

        assertEquals(userLessonDto, result);
    }

    @Test
    void getById_lessonNotFound_throwException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.getById(1L));
    }

    @Test
    void getAll_returnListOfLessons() {
        when(lessonRepository.findAll()).thenReturn(List.of(lesson));
        when(lessonMapper.modelToAdminDto(lesson)).thenReturn(adminLessonDto);

        List<AdminLessonDto> result = lessonService.getAll();

        assertEquals(1, result.size());
        assertEquals(adminLessonDto, result.get(0));
    }

    @Test
    void getAllPublishedByInternshipId_returnAllPublishedLessons() {
        lesson.setIsPublished(true);

        when(lessonRepository.findByIsPublishedTrueAndInternshipId(1L)).thenReturn(List.of(lesson));
        when(lessonMapper.modelToUserDto(lesson)).thenReturn(userLessonDto);

        List<UserLessonDto> result = lessonService.getAllPublishedByInternshipId(1L);

        assertEquals(1, result.size());
        assertEquals(userLessonDto, result.get(0));
    }

    @Test
    void publish_setIsPublishedTrue() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.publish(1L);

        verify(lessonRepository, times(1)).saveAndFlush(lesson);
        assertTrue(lesson.getIsPublished());
    }

    @Test
    void publish_lessonNotFound_throwException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.publish(1L));
    }

    @Test
    void publish_lessonAlreadyPublished_throwException() {
        lesson.setIsPublished(true);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        assertThrows(AlreadyPublishedException.class, () -> lessonService.publish(1L));
    }
}