package org.example.internship.service.task;

import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;

import java.util.List;

/**
 * Сервис для работы с заданиями.
 */
public interface TaskService {

    /**
     * Сохранение нового задания и создание репозитория для него.
     *
     * @param taskDto данные нового задания
     */
    void save(NewTaskDto taskDto);

    /**
     * Получение списка всех опубликованных заданий.
     *
     * @return список всех опубликованных заданий
     */
    List<TaskDto> getAllPublished();

    /**
     * Получение информации о задании по его идентификатору.
     *
     * @param id идентификатор задания
     * @return информация о задании
     */
    TaskDto getById(Long id);

    /**
     * Обновление информацию о задании.
     *
     * @param taskDto данные обновленного задания
     */
    void update(UpdateTaskDto taskDto);

    /**
     * Получение списка всех заданий.
     *
     * @return список всех заданий
     */
    List<TaskDto> getAll();

    /**
     * Публикация задания по его идентификатору.
     *
     * @param id идентификатор задания
     */
    void publishById(Long id);

    /**
     * Публикация заданий по идентификатору занятия, к которому они принадлежат.
     *
     * @param lessonId идентификатор занятия
     */
    void publishByLessonId(Long lessonId);

}
