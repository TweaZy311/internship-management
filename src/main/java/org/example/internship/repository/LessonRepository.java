package org.example.internship.repository;

import org.example.internship.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с занятиями.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Поиск опубликованных занятий.
     *
     * @return список опубликованных занятий
     */
    List<Lesson> findByIsPublishedTrue();

    /**
     * Поиск опубликованных занятий по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список опубликованных занятий для указанной стажировки
     */
    List<Lesson> findByIsPublishedTrueAndInternshipId(Long internshipId);
}
