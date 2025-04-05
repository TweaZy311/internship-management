package org.example.internship.repository;

import org.example.internship.entity.InternshipEntity;
import org.example.internship.entity.LessonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с занятиями.
 */
@Repository
public interface LessonRepository extends PagingAndSortingRepository<LessonEntity, Long>, JpaSpecificationExecutor<LessonEntity>, JpaRepository<LessonEntity, Long> {

    /**
     * Поиск опубликованных занятий.
     *
     * @return список опубликованных занятий
     */
    List<LessonEntity> findByIsPublished(boolean isPublished);

    /**
     * Поиск опубликованных занятий по идентификатору стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @return список опубликованных занятий для указанной стажировки
     */
    List<LessonEntity> findByIsPublishedAndInternshipId(boolean isPublished, Long internshipId);
}
