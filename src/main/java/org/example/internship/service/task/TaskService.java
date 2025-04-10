package org.example.internship.service.task;

import org.example.internship.entity.TaskEntity;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.model.response.task.Task;
import org.springframework.data.domain.Page;

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
    Task saveTask(CreateTaskRequest taskDto);

    /**
     * Получение списка всех опубликованных заданий.
     *
     * @return список всех опубликованных заданий
     */
    List<Task> getAllPublished();

    /**
     * Получение информации о задании по его идентификатору.
     *
     * @param id идентификатор задания
     * @return информация о задании
     */
    Task getTaskById(Long id);

    /**
     * Обновление информацию о задании.
     *
     * @param taskDto данные обновленного задания
     */
    Task updateTask(UpdateTaskRequest taskDto);

    /**
     * Получение списка всех заданий.
     *
     * @return список всех заданий
     */
    Page<TaskEntity> getTasks(BaseGetListRequest request);

    /**
     * Публикация задания по его идентификатору.
     *
     * @param id идентификатор задания
     */
    Task publishById(Long id);

    /**
     * Публикация заданий по идентификатору занятия, к которому они принадлежат.
     *
     * @param lessonId идентификатор занятия
     */
    List<Task> publishByLessonId(Long lessonId);

}
