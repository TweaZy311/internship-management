package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;
import org.example.internship.service.lesson.LessonService;
import org.example.internship.service.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Создание нового занятия.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param lesson информация о новом занятии
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
    public ResponseEntity<Void> createLesson(@RequestBody NewLessonDto lesson) {
        lessonService.save(lesson);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
    public ResponseEntity<UserLessonDto> getLessonById(@PathVariable Long id) {
        return new ResponseEntity<>(lessonService.getById(id), HttpStatus.OK);
    }

    /**
     * Получение списка всех занятий.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return HTTP-ответ со списком всех занятий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех занятий",
            description = "Возвращает список всех занятий. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список всех занятий"),
            @ApiResponse(responseCode = "204", description = "Список занятий пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<List<AdminLessonDto>> getAllLessons() {
        List<AdminLessonDto> lessons = lessonService.getAll();
        if (lessons.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

    /**
     * Получение списка опубликованных занятий по идентификатору программы стажировки.
     * Доступно только пользователям с ролью ADMIN или USER.
     *
     * @param internshipId идентификатор программы стажировки
     * @return HTTP-ответ со списком опубликованных занятий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
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
    public ResponseEntity<List<UserLessonDto>> getPublishedLessons(@RequestParam Long internshipId) {
        List<UserLessonDto> lessons = lessonService.getAllPublishedByInternshipId(internshipId);
        if (lessons.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
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
            @ApiResponse(responseCode = "409", description = "Занятие уже было опубликовано ранее"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор задания", required = true)
    public ResponseEntity<Void> publishLesson(@PathVariable Long id) {
        lessonService.publish(id);
        taskService.publishByLessonId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
