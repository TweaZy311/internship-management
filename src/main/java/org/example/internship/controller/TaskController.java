package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.service.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с заданиями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление заданиями")
public class TaskController {
    private final TaskService taskService;

    /**
     * Создание нового задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param task информация о новом задании
     * @return HTTP-ответ с кодом состояния 201 CREATED в случае успешного создания задания
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новое задание",
            description = "Создает новое задание. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задание успешно создано"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация о новом задании", required = true)
    public ResponseEntity<Void> createTask(@RequestBody NewTaskDto task) {
        taskService.save(task);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Получение всех опубликованных заданий.
     * Доступно только пользователям с ролью ADMIN или USER.
     *
     * @return HTTP-ответ со списком опубликованных заданий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список опубликованных заданий",
            description = "Возвращает список всех опубликованных заданий. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заданий успешно получен"),
            @ApiResponse(responseCode = "204", description = "Опубликованные задания не найдены"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<List<TaskDto>> getAllPublishedTasks() {
        List<TaskDto> tasks = taskService.getAllPublished();
        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Публикация задания по его идентификатору.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param id идентификатор задания для публикации
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешной публикации задания
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Опубликовать задание по ID",
            description = "Публикует задание по его идентификатору. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задание успешно опубликовано"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "409", description = "Задание уже опубликовано"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<Void> publishTaskById(@PathVariable Long id) {
        taskService.publishById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Публикация заданий по идентификатору занятия.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param lessonId идентификатор занятия, к которому относятся задания
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешной публикации задания
     */
    @PatchMapping("/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Опубликовать задания по ID занятия",
            description = "Публикует все задания, связанные с указанным ID занятия. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задания успешно опубликованы"),
            @ApiResponse(responseCode = "404", description = "Занятие не найдено"),
            @ApiResponse(responseCode = "409", description = "Занятие, к которому относятся задания" +
                    "еще не опубликовано"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")

    })
    @Parameter(name = "lessonId", description = "ID занятия", required = true)
    public ResponseEntity<Void> publishTasksByLessonId(@RequestParam Long lessonId) {
        taskService.publishByLessonId(lessonId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получение всех заданий.
     *
     * @return HTTP-ответ со списком всех заданий и кодом состояния 200 OK в случае успешного получения данных
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех заданий",
            description = "Возвращает список всех заданий. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заданий успешно получен"),
            @ApiResponse(responseCode = "204", description = "Список заданий пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAll();
        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Обновление задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param task информация о задании, которое нужно обновить
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного обновления задания
     */
    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить задание",
            description = "Обновляет информацию о задании. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задание успешно обновлено"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация об обновленном задании", required = true)
    public ResponseEntity<Void> updateTask(@RequestBody UpdateTaskDto task) {
        taskService.update(task);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Получение задания по его идентификатору.
     * Доступно пользователям с ролью ADMIN или USER.
     *
     * @param id идентификатор задания
     * @return HTTP-ответ с информацией о задании и кодом состояния 200 OK в случае успешного получения данных
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию о задании",
            description = "Возвращает информацию о задании по его идентификатору. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о задании успешно получена"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор задания", required = true)
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return new ResponseEntity<>(taskService.getById(id), HttpStatus.OK);
    }
}
