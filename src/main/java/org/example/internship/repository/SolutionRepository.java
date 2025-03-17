package org.example.internship.repository;

import org.example.internship.entity.SolutionEntity;
import org.example.internship.entity.TaskEntity;
import org.example.internship.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с решениями задач.
 */
@Repository
public interface SolutionRepository extends JpaRepository<SolutionEntity, Long> {

    /**
     * Поиск решения по URL репозитория.
     *
     * @param url URL репозитория
     * @return решение, найденное по URL
     */
    SolutionEntity findByRepositoryUrl(String url);

    /**
     * Поиск не архивированных решений по статусу.
     *
     * @param status статус решения
     * @return список решений с указанным статусом
     */
    List<SolutionEntity> findAllByStatusIdAndIsArchived(Long status, Boolean isArchived);

    /**
     * Поиск решений для заданного пользователя и списка задач.
     *
     * @param user  пользователь
     * @param tasks список задач
     * @return список решений для указанного пользователя и задач
     */
    List<SolutionEntity> findAllByUserAndTaskIn(UserEntity user, List<TaskEntity> tasks);

    /**
     * Поиск не архивированных решений для задачи по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return список решений для указанной задачи
     */
    List<SolutionEntity> findAllByTaskIdAndIsArchivedFalse(Long taskId);

    /**
     * Поиск решений по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список решений для указанного пользователя
     */
    List<SolutionEntity> findAllByUserId(Long userId);
}
