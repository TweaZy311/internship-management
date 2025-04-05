package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.LessonEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.example.internship.model.response.lesson.AdminLessonInfo;
import org.example.internship.model.response.lesson.Lesson;
import org.example.internship.model.response.lesson.UserLessonInfo;
import org.example.internship.service.lesson.LessonService;
import org.example.internship.service.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с занятиями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление занятиями")
public class LessonController {
    private final LessonService lessonService;
    private final TaskService taskService;
    private final Mapper mapper;

    /**
     * Создание нового занятия.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request информация о новом занятии
     * @return HTTP-ответ с кодом состояния 201 CREATED в случае успешного создания занятия
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новое занятие",
            description = "Создает новое занятие. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Занятие успешно создано"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация о новом занятии", required = true)
    public ResponseEntity<Lesson> createLesson(@RequestBody CreateLessonRequest request) {
        AdminLessonInfo lesson = lessonService.saveLesson(request);
        return new ResponseEntity<>(lesson, HttpStatus.CREATED);
    }

    /**
     * Получение информации о занятии по его идентификатору.
     * Доступно только пользователям с ролью ADMIN или USER.
     *
     * @param id идентификатор занятия
     * @return HTTP-ответ с информацией о занятии и кодом состояния 200 OK в случае успешного получения данных
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию о занятии",
            description = "Возвращает информацию о занятии по его идентификатору. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о занятии"),
            @ApiResponse(responseCode = "404", description = "Занятие не найдено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор занятия", required = true)
    public ResponseEntity<?> getLessonById(@RequestParam(name = "private", defaultValue = "false") Boolean isPrivate,
            @PathVariable Long id) {
        LessonEntity lesson = lessonService.getLessonById(id);
        if (isPrivate) {
            return new ResponseEntity<>(mapper.map(lesson, AdminLessonInfo.class), HttpStatus.OK);
        }
        return new ResponseEntity<>(mapper.map(lesson, UserLessonInfo.class), HttpStatus.OK);
    }

    /**
     * Получение списка всех занятий.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return HTTP-ответ со списком всех занятий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @PostMapping("/page")
    @Operation(summary = "Получить список всех занятий",
            description = "Возвращает список всех занятий.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список всех занятий"),
            @ApiResponse(responseCode = "204", description = "Список занятий пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @PreAuthorize("@securityService.hasAccess(#isPrivate)")
    public ResponseEntity<?> getLessons(@RequestParam(name = "private", defaultValue = "false") Boolean isPrivate,
                                        @RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<LessonEntity> page = lessonService.getLessons(request);

        if (isPrivate){
            Page<AdminLessonInfo> result = Page.<AdminLessonInfo>builder()
                    .pageSize(page.getSize())
                    .pageNumber(page.getNumber())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .content(mapper.mapAsList(page.getContent(), AdminLessonInfo.class))
                    .build();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        Page<UserLessonInfo> result = Page.<UserLessonInfo>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), UserLessonInfo.class))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Получение списка опубликованных занятий по идентификатору программы стажировки.
     * Доступно только пользователям с ролью ADMIN или USER.
     *
     * @param internshipId идентификатор программы стажировки
     * @return HTTP-ответ со списком опубликованных занятий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */

    //todo deprecated
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить список опубликованных занятий",
            description = "Возвращает список опубликованных занятий по идентификатору программы стажировки. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список опубликованных занятий"),
            @ApiResponse(responseCode = "204", description = "Список опубликованных занятий пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "internshipId", description = "Идентификатор стажировки", required = true)
    public ResponseEntity<List<UserLessonInfo>> getPublishedLessons(@RequestParam Long internshipId) {
        List<UserLessonInfo> lessons = lessonService.getAllPublishedByInternshipId(internshipId);
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

    /**
     * Публикация занятия вместе с существующими к нему заданиями.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param id идентификатор занятия, которое нужно опубликовать
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешной публикации занятия
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Опубликовать занятие",
            description = "Публикует занятие и связанные с ним задания. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Занятие успешно опубликовано"),
            @ApiResponse(responseCode = "400", description = "Занятие уже было опубликовано ранее"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор задания", required = true)
    @Transactional
    public ResponseEntity<Void> publishLesson(@PathVariable Long id) {
        lessonService.publishLesson(id);
        //todo
        taskService.publishByLessonId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
