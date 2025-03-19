package org.example.internship.service.solution;

import org.example.internship.model.request.solution.UpdateSolutionStatusRequest;
import org.example.internship.model.response.solution.Solution;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;

import java.util.List;

/**
 * Сервис для работы с решениями заданий.
 */
public interface SolutionService {

    /**
     * Добавление решения задания на основе события системного хука GitLab.
     *
     * @param pushEvent событие системного хука GitLab
     */
    Solution addSolution(PushSystemHookEvent pushEvent);

    /**
     * Обновление статуса решения задания.
     *
     * @param solution информация о решении и его новом статусе
     */
    Solution updateSolutionStatus(UpdateSolutionStatusRequest solution);

    /**
     * Получение информации о решении по его идентификатору.
     *
     * @param id идентификатор решения
     * @return информация о решении
     */
    Solution getSolutionById(Long id);

    /**
     * Получение списка всех решений.
     *
     * @return список всех решений
     */
    List<Solution> getAllSolutions();

    /**
     * Получение списка решений по указанному статусу.
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    List<Solution> getAllByStatus(String status);


    /**
     * Получение списка всех решений задания по его идентификатору.
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    List<Solution> getAllByTaskId(Long taskId);

    /**
     * Архивирование решений задач по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void archiveSolutions(Long userId);
}
