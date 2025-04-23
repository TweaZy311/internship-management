package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.LessonEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.example.internship.model.response.lesson.AdminLessonInfo;
import org.example.internship.model.response.lesson.Lesson;
import org.example.internship.model.response.lesson.UserLessonInfo;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.lesson.LessonService;
import org.example.internship.service.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления занятиями в рамках программы стажировок.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление занятиями", description = "Операции для администрирования и получения информации о занятиях")
public class LessonController {

    private final LessonService lessonService;
    private final TaskService taskService;
    private final AuditService auditService;

    private final Mapper mapper;

    /**
     * Создание нового занятия.
     *
     * @param request  информация о создаваемом занятии
     * @param username имя пользователя, выполнившего запрос
     * @return созданное занятие
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новое занятие", description = "Создаёт новое занятие. Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Занятие успешно создано",
                    content = @Content(schema = @Schema(implementation = AdminLessonInfo.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @RequestBody(description = "Информация о новом занятии", required = true,
            content = @Content(schema = @Schema(implementation = CreateLessonRequest.class)))
    public ResponseEntity<AdminLessonInfo> createLesson(@org.springframework.web.bind.annotation.RequestBody CreateLessonRequest request,
                                                        @RequestHeader("username") String username) {
        LessonEntity lesson = lessonService.saveLesson(request);
        auditService.addRecord(AuditEntityType.LESSON, AuditActionType.CREATE, lesson.getId(), lesson.getName(), username);
        return new ResponseEntity<>(mapper.map(lesson, AdminLessonInfo.class), HttpStatus.CREATED);
    }

    /**
     * Получение информации о занятии по идентификатору.
     *
     * @param id        идентификатор занятия
     * @param isPrivate если true, возвращается полная административная информация
     * @return информация о занятии
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию о занятии", description = "Возвращает информацию о занятии по его ID. В зависимости от параметра `private` возвращает полную или общую информацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о занятии",
                    content = {
                            @Content(schema = @Schema(implementation = AdminLessonInfo.class)),
                            @Content(schema = @Schema(implementation = UserLessonInfo.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
            @ApiResponse(responseCode = "404", description = "Занятие не найдено")
    })
    public ResponseEntity<?> getLessonById(
            @RequestParam(name = "private", defaultValue = "false") Boolean isPrivate,
            @Parameter(description = "Идентификатор занятия", required = true) @PathVariable Long id) {
        LessonEntity lesson = lessonService.getLessonById(id);
        if (isPrivate) {
            return new ResponseEntity<>(mapper.map(lesson, AdminLessonInfo.class), HttpStatus.OK);
        }
        return new ResponseEntity<>(mapper.map(lesson, UserLessonInfo.class), HttpStatus.OK);
    }

    /**
     * Получение всех занятий с пагинацией.
     *
     * @param isPrivate если true, возвращаются административные данные
     * @param request   параметры пагинации и фильтрации
     * @return страница занятий
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех занятий", description = "Возвращает страницу занятий с параметрами фильтрации и пагинации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список всех занятий",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<Page<AdminLessonInfo>> getLessons(
            @RequestParam(name = "private", defaultValue = "false") Boolean isPrivate,
            @RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<LessonEntity> page = lessonService.getLessons(request);
        Page<AdminLessonInfo> result = Page.<AdminLessonInfo>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), AdminLessonInfo.class))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Получение опубликованных занятий по стажировке.
     *
     * @param internshipId идентификатор программы стажировки
     * @return список опубликованных занятий
     */
    @Deprecated
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список опубликованных занятий", description = "Возвращает опубликованные занятия по ID стажировки. Устаревший метод.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список опубликованных занятий",
                    content = @Content(schema = @Schema(implementation = AdminLessonInfo.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<List<AdminLessonInfo>> getPublishedLessons(
            @Parameter(description = "Идентификатор стажировки", required = true) @RequestParam Long internshipId) {
        List<LessonEntity> lessons = lessonService.getAllPublishedByInternshipId(internshipId);
        return new ResponseEntity<>(mapper.mapAsList(lessons, AdminLessonInfo.class), HttpStatus.OK);
    }

    /**
     * Публикация занятия и связанных заданий.
     *
     * @param id       идентификатор занятия
     * @param username имя пользователя, выполняющего действие
     * @return статус выполнения операции
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Operation(summary = "Опубликовать занятие", description = "Публикует занятие и связанные с ним задания. Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Занятие успешно опубликовано"),
            @ApiResponse(responseCode = "400", description = "Занятие уже опубликовано"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    public ResponseEntity<Void> publishLesson(
            @Parameter(description = "Идентификатор занятия", required = true) @PathVariable Long id,
            @RequestHeader("username") String username) {
        LessonEntity lesson = lessonService.publishLesson(id);
        //todo
        taskService.publishByLessonId(id);
        auditService.addRecord(AuditEntityType.LESSON, AuditActionType.UPDATE, lesson.getId(), lesson.getName(), username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
