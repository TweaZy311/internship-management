package org.example.internship.repository;

import org.example.internship.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий для работы с задачами.
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    /**
     * Поиск всех задач, опубликованных до указанной даты.
     *
     * @param date дата публикации
     * @return список задач, опубликованных до указанной даты
     */
    List<TaskEntity> findAllByPublishDateLessThanEqual(LocalDate date);

    /**
     * Поиск всех задач для стажировки с указанным идентификатором.
     *
     * @param internshipId идентификатор стажировки
     * @return список задач для указанной стажировки
     */
    List<TaskEntity> findAllByLesson_InternshipId(Long internshipId);

    /**
     * Поиск всех задач для занятия с указанным идентификатором, у которых дата публикации не установлена.
     *
     * @param lessonId идентификатор урока
     * @return список неопубликованных задач для указанного урока
     */
    List<TaskEntity> findAllByLessonIdAndPublishDateIsNull(Long lessonId);

    /**
     * Поиск задачи по имени.
     *
     * @param name имя задачи
     * @return задача с указанным именем
     */
    TaskEntity findByName(String name);
}
