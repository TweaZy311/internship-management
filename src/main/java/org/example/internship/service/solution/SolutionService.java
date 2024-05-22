package org.example.internship.service.solution;

import org.example.internship.dto.request.solution.SolutionStatusDto;
import org.example.internship.dto.response.solution.SolutionDto;
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
    void add(PushSystemHookEvent pushEvent);

    /**
     * Обновление статуса решения задания.
     *
     * @param solution информация о решении и его новом статусе
     */
    void updateStatus(SolutionStatusDto solution);

    /**
     * Получение информации о решении по его идентификатору.
     *
     * @param id идентификатор решения
     * @return информация о решении
     */
    SolutionDto getById(Long id);

    /**
     * Получение списка всех решений.
     *
     * @return список всех решений
     */
    List<SolutionDto> getAll();

    /**
     * Получение списка решений по указанному статусу.
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    List<SolutionDto> getAllByStatus(String status);


    /**
     * Получение списка всех решений задания по его идентификатору.
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    List<SolutionDto> getAllByTaskId(Long taskId);

    /**
     * Архивирование решений задач по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void archiveSolutions(Long userId);
}
