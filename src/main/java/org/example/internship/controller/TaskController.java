package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.TaskEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.*;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.model.response.task.Task;
import org.example.internship.service.audit.AuditService;
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
    private final AuditService auditService;

    private final Mapper mapper;

    /**
     * Создание нового задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request информация о новом задании
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
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request,
                                           @RequestHeader("username") String username) {
        TaskEntity task = taskService.saveTask(request);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.CREATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.CREATED);
    }

    /**
     * Получение всех опубликованных заданий.
     * Доступно только пользователям с ролью ADMIN или USER.
     *
     * @return HTTP-ответ со списком опубликованных заданий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    //todo deprecated
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список опубликованных заданий",
            description = "Возвращает список всех опубликованных заданий. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заданий успешно получен"),
            @ApiResponse(responseCode = "204", description = "Опубликованные задания не найдены"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<List<Task>> getAllPublishedTasks() {
        List<TaskEntity> tasks = taskService.getAllPublished();
        return new ResponseEntity<>(mapper.mapAsList(tasks, Task.class), HttpStatus.OK);
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
    public ResponseEntity<Task> publishTaskById(@PathVariable Long id,
                                                @RequestHeader("username") String username) {
        TaskEntity task = taskService.publishById(id);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
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
    public ResponseEntity<List<Task>> publishTasksByLessonId(@RequestParam Long lessonId,
                                                             @RequestHeader("username") String username) {
        List<TaskEntity> tasks = taskService.publishByLessonId(lessonId);
        //todo как быть здесь?
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, null, null, username);
        return new ResponseEntity<>(mapper.mapAsList(tasks, Task.class), HttpStatus.OK);
    }

    /**
     * Получение всех заданий.
     *
     * @return HTTP-ответ со списком всех заданий и кодом состояния 200 OK в случае успешного получения данных
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @PostMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список всех заданий",
            description = "Возвращает список всех заданий. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заданий успешно получен"),
            @ApiResponse(responseCode = "204", description = "Список заданий пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<Page<Task>> getTasks(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<TaskEntity> page = taskService.getTasks(request);

        //TODO FIXME
        if (false) { //если пользователь не админ то возвращем только опубликованные
            request.getFilters()
                    .add(new SearchCriteria(SearchKey.IS_PUBLISHED, SearchOperation.EQ, Boolean.TRUE, BooleanOperator.AND));
        }

        Page<Task> result = Page.<Task>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Task.class))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обновление задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request информация о задании, которое нужно обновить
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
    public ResponseEntity<Task> updateTask(@RequestBody UpdateTaskRequest request,
                                           @RequestHeader("username") String username) {
        TaskEntity task = taskService.updateTask(request);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
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
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        TaskEntity task = taskService.getTaskById(id);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
    }
}
