package org.example.internship.repository;

import org.example.internship.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с задачами.
 */
@Repository
public interface TaskRepository extends PagingAndSortingRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity>, JpaRepository<TaskEntity, Long> {

    /**
     * Поиск всех опубликованных задач.
     *
     * @param isPublished флаг публикации
     * @return список опубликованных задач
     */
    List<TaskEntity> findAllByIsPublished(Boolean isPublished);

    /**
     * Поиск всех задач для стажировки с указанным идентификатором.
     *
     * @param internshipId идентификатор стажировки
     * @return список задач для указанной стажировки
     */
    List<TaskEntity> findAllByLesson_InternshipId(Long internshipId);

    /**
     * Поиск всех задач для занятия с указанным идентификатором и статусом публикации.
     *
     * @param lessonId идентификатор урока
     * @param isPublished флаг публикации
     * @return список неопубликованных задач для указанного урока
     */
    List<TaskEntity> findAllByLessonIdAndIsPublished(Long lessonId, Boolean isPublished);

    /**
     * Поиск задачи по имени.
     *
     * @param name имя задачи
     * @return задача с указанным именем
     */
    TaskEntity findByName(String name);
}
