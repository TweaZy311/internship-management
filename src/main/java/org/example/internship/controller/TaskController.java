package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.TaskEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
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
 * Контроллер для управления заданиями.
 * Предоставляет REST API для создания, публикации, получения и обновления заданий.
 * Все действия доступны только авторизованным пользователям с соответствующими ролями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление заданиями", description = "Операции создания, получения, публикации и обновления заданий.")
public class TaskController {

    private final TaskService taskService;
    private final AuditService auditService;
    private final Mapper mapper;

    /**
     * Создаёт новое задание.
     *
     * @param request  информация о задании
     * @param username имя пользователя, выполняющего операцию
     * @return созданное задание
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новое задание", description = "Создает новое задание. Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задание успешно создано", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация о новом задании", required = true, content = @Content(schema = @Schema(implementation = CreateTaskRequest.class)))
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request,
                                           @RequestHeader("username") String username) {
        TaskEntity task = taskService.saveTask(request);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.CREATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.CREATED);
    }

    /**
     * Получает список всех опубликованных заданий.
     *
     * @return список опубликованных заданий
     */
    @Deprecated
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список опубликованных заданий", description = "Возвращает список всех опубликованных заданий.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список найден", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    public ResponseEntity<List<Task>> getAllPublishedTasks() {
        List<TaskEntity> tasks = taskService.getAllPublished();
        return new ResponseEntity<>(mapper.mapAsList(tasks, Task.class), HttpStatus.OK);
    }

    /**
     * Публикует задание по его ID.
     *
     * @param id       идентификатор задания
     * @param username имя пользователя
     * @return опубликованное задание
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Опубликовать задание по ID", description = "Публикует задание по его идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание опубликовано", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "409", description = "Уже опубликовано"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @Parameter(name = "id", description = "Идентификатор задания для публикации", required = true)
    public ResponseEntity<Task> publishTaskById(@PathVariable Long id,
                                                @RequestHeader("username") String username) {
        TaskEntity task = taskService.publishById(id);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
    }

    /**
     * Публикует все задания, связанные с конкретным занятием.
     *
     * @param lessonId идентификатор занятия
     * @param username имя пользователя
     * @return список опубликованных заданий
     */
    @PatchMapping("/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Опубликовать задания по ID занятия", description = "Публикует все задания, связанные с указанным ID занятия.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задания опубликованы", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Занятие не найдено"),
            @ApiResponse(responseCode = "409", description = "Занятие еще не опубликовано"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @Parameter(name = "lessonId", description = "Идентификатор занятия, для которого нужно опубликовать задания", required = true)
    public ResponseEntity<List<Task>> publishTasksByLessonId(@RequestParam Long lessonId,
                                                             @RequestHeader("username") String username) {
        List<TaskEntity> tasks = taskService.publishByLessonId(lessonId);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, null, null, username); // Неизвестные taskId и имя
        return new ResponseEntity<>(mapper.mapAsList(tasks, Task.class), HttpStatus.OK);
    }

    /**
     * Получает все задания с пагинацией.
     *
     * @param request параметры фильтрации и пагинации
     * @return страница с заданиями
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех заданий", description = "Возвращает список всех заданий с постраничной разбивкой.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    public ResponseEntity<Page<Task>> getTasks(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<TaskEntity> page = taskService.getTasks(request);

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
     * Обновляет информацию о задании.
     *
     * @param request  данные для обновления
     * @param username имя пользователя
     * @return обновлённое задание
     */
    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить задание", description = "Обновляет информацию о задании.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание обновлено", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация об обновленном задании", required = true, content = @Content(schema = @Schema(implementation = UpdateTaskRequest.class)))
    public ResponseEntity<Task> updateTask(@RequestBody UpdateTaskRequest request,
                                           @RequestHeader("username") String username) {
        TaskEntity task = taskService.updateTask(request);
        auditService.addRecord(AuditEntityType.TASK, AuditActionType.UPDATE, task.getId(), task.getName(), username);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
    }

    /**
     * Получает задание по его идентификатору.
     *
     * @param id идентификатор задания
     * @return задание
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить задание по ID", description = "Возвращает информацию о задании по его идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание найдено", content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @Parameter(name = "id", description = "Идентификатор задания", required = true)
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        TaskEntity task = taskService.getTaskById(id);
        return new ResponseEntity<>(mapper.map(task, Task.class), HttpStatus.OK);
    }
}
