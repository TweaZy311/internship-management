package org.example.internship.service.lesson;

import org.example.internship.entity.LessonEntity;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.springframework.data.domain.Page;

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
    LessonEntity saveLesson(CreateLessonRequest createLessonRequest);

    /**
     * Получение информации о занятии по его идентификатору.
     *
     * @param id идентификатор занятия
     * @return информация о занятии
     */
    LessonEntity getLessonById(Long id);

    /**
     * Публикация занятия по его идентификатору.
     *
     * @param id идентификатор занятия
     */
    LessonEntity publishLesson(Long id);

    /**
     * Получение списка всех опубликованных занятий в рамках стажировки.
     *
     * @param id идентификатор стажировки
     * @return список опубликованных занятий
     */
    List<LessonEntity> getAllPublishedByInternshipId(Long id);

    /**
     * Получение списка всех занятий.
     *
     * @return список всех занятий
     */
    Page<LessonEntity> getLessons(BaseGetListRequest request);
}
