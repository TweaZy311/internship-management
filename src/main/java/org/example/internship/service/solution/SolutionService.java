package org.example.internship.service.solution;

import org.example.internship.entity.SolutionEntity;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.solution.UpdateSolutionStatusRequest;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.data.domain.Page;

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
    SolutionEntity addSolution(PushSystemHookEvent pushEvent);

    /**
     * Обновление статуса решения задания.
     *
     * @param solution информация о решении и его новом статусе
     */
    SolutionEntity updateSolutionStatus(UpdateSolutionStatusRequest solution);

    /**
     * Получение информации о решении по его идентификатору.
     *
     * @param id идентификатор решения
     * @return информация о решении
     */
    SolutionEntity getSolutionById(Long id);

    /**
     * Получение списка всех решений.
     *
     * @return список всех решений
     */
    Page<SolutionEntity> getSolutions(BaseGetListRequest request);

    /**
     * Получение списка решений по указанному статусу.
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    List<SolutionEntity> getAllByStatus(String status);


    /**
     * Получение списка всех решений задания по его идентификатору.
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    List<SolutionEntity> getAllByTaskId(Long taskId);

    /**
     * Архивирование решений задач по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void archiveSolutions(Long userId);
}
