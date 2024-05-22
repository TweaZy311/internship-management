package org.example.internship.service.lesson;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;
import org.example.internship.exception.AlreadyPublishedException;
import org.example.internship.mapper.LessonMapper;
import org.example.internship.model.Lesson;
import org.example.internship.repository.LessonRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с занятиями.
 */
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    /**
     * {@inheritDoc}
     *
     * @param newLessonDto информация о новом занятии
     */
    @Override
    public void save(NewLessonDto newLessonDto) {
        Lesson lesson = lessonMapper.newLessonDtoToModel(newLessonDto);
        lessonRepository.save(lesson);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор занятия
     * @return информация о занятии
     * @throws EntityNotFoundException если занятие не найдено
     */
    @Override
    public UserLessonDto getById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
        return lessonMapper.modelToUserDto(lesson);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех занятий
     */
    @Override
    public List<AdminLessonDto> getAll() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream()
                .map(lessonMapper::modelToAdminDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return список опубликованных занятий
     */
    @Override
    public List<UserLessonDto> getAllPublishedByInternshipId(Long internshipId) {
        List<Lesson> publishedLessons = lessonRepository.findByIsPublishedTrueAndInternshipId(internshipId);
        return publishedLessons.stream()
                .map(lessonMapper::modelToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор занятия
     * @throws EntityNotFoundException если занятие не найдено
     */
    @Override
    public void publish(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
        if (lesson.getIsPublished()) {
            throw new AlreadyPublishedException("Lesson is already published with id: " + id);
        }
        lesson.setIsPublished(true);
        lessonRepository.saveAndFlush(lesson);
    }
}
