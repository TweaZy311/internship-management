package org.example.internship.service.lesson;

import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.example.internship.model.response.lesson.AdminLessonInfo;
import org.example.internship.model.response.lesson.UserLessonInfo;

import java.util.List;

/**
 * Сервис для работы с занятиями.
 */
public interface LessonService {

    /**
     * Сохранение нового занятия.
     *
     * @param createLessonRequest информация о новом занятии
     */
    AdminLessonInfo save(CreateLessonRequest createLessonRequest);

    /**
     * Получение информации о занятии по его идентификатору.
     *
     * @param id идентификатор занятия
     * @return информация о занятии
     */
    UserLessonInfo getById(Long id);

    /**
     * Публикация занятия по его идентификатору.
     *
     * @param id идентификатор занятия
     */
    AdminLessonInfo publish(Long id);

    /**
     * Получение списка всех опубликованных занятий в рамках стажировки.
     *
     * @param id идентификатор стажировки
     * @return список опубликованных занятий
     */
    List<UserLessonInfo> getAllPublishedByInternshipId(Long id);

    /**
     * Получение списка всех занятий.
     *
     * @return список всех занятий
     */
    List<AdminLessonInfo> getAll();
}
