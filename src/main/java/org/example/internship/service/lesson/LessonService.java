package org.example.internship.service.lesson;

import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;

import java.util.List;

/**
 * Сервис для работы с занятиями.
 */
public interface LessonService {

    /**
     * Сохранение нового занятия.
     *
     * @param newLessonDto информация о новом занятии
     */
    void save(NewLessonDto newLessonDto);

    /**
     * Получение информации о занятии по его идентификатору.
     *
     * @param id идентификатор занятия
     * @return информация о занятии
     */
    UserLessonDto getById(Long id);

    /**
     * Публикация занятия по его идентификатору.
     *
     * @param id идентификатор занятия
     */
    void publish(Long id);

    /**
     * Получение списка всех опубликованных занятий в рамках стажировки.
     *
     * @param id идентификатор стажировки
     * @return список опубликованных занятий
     */
    List<UserLessonDto> getAllPublishedByInternshipId(Long id);

    /**
     * Получение списка всех занятий.
     *
     * @return список всех занятий
     */
    List<AdminLessonDto> getAll();
}
