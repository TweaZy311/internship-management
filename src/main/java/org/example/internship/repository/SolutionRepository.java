package org.example.internship.repository;

import org.example.internship.model.task.Solution;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с решениями задач.
 */
@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    /**
     * Поиск решения по URL репозитория.
     *
     * @param url URL репозитория
     * @return решение, найденное по URL
     */
    Solution findByRepositoryUrl(String url);

    /**
     * Поиск не архивированных решений по статусу.
     *
     * @param status статус решения
     * @return список решений с указанным статусом
     */
    List<Solution> findAllByStatusAndIsArchivedFalse(SolutionStatus status);

    /**
     * Поиск решений для заданного пользователя и списка задач.
     *
     * @param user  пользователь
     * @param tasks список задач
     * @return список решений для указанного пользователя и задач
     */
    List<Solution> findAllByUserAndTaskIn(User user, List<Task> tasks);

    /**
     * Поиск не архивированных решений для задачи по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return список решений для указанной задачи
     */
    List<Solution> findAllByTaskIdAndIsArchivedFalse(Long taskId);

    /**
     * Поиск решений по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список решений для указанного пользователя
     */
    List<Solution> findAllByUserId(Long userId);
}
