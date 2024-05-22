package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.exception.ExceptionResponse;
import org.example.internship.service.application.ApplicationService;
import org.example.internship.service.internship.InternshipService;
import org.example.internship.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


/**
 * Класс контроллера для управления заявками на стажировку.
 * Этот контроллер предоставляет эндпоинты для создания, обновления и получения заявок на стажировку.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
@Tag(name = "Управление заявками")
public class ApplicationController {
    private final ApplicationService applicationService;
    private final Validator validator;
    private final InternshipService internshipService;

    /**
     * Создание новой заявки на стажировку.
     * Перед сохранением заявки производится валидация формата электронной почты и номера телефона.
     *
     * @param application Объект NewApplicationDto, содержащий данные заявки.
     * @return ResponseEntity с HTTP-статусом 201 CREATED, если заявка успешно создана,
     * или ResponseEntity с HTTP-статусом 400 BAD REQUEST, если формат электронной почты или номера телефона неверный.
     */
    @PostMapping("/create")
    @Operation(summary = "Создать новую заявку на стажировку",
            description = "Создает новую заявку на стажировку. Проверяет формат электронной почты и номера телефона.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверный формат электронной почты или номера телефона, или регистрация закрыта")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные новой заявки", required = true)
    public ResponseEntity<ExceptionResponse> createApplication(
            @RequestBody NewApplicationDto application) {
        PublicInternshipDto internshipDto = internshipService.getById(application.getInternshipId());
        ExceptionResponse exceptionResponse;

        if (internshipDto.getRegistrationEndDate().isBefore(LocalDate.now())) {
            exceptionResponse = new ExceptionResponse("Registration for the internship is closed");
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
        if (!validator.emailIsValid(application.getEmail())) {
            exceptionResponse = new ExceptionResponse("Wrong e-mail format");
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
        if (!validator.phoneNumberIsValid(application.getPhoneNumber())) {
            exceptionResponse = new ExceptionResponse("Wrong phone number format");
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
        applicationService.save(application);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Обновление статуса заявки на стажировку.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param statusDto Объект ApplicationStatusDto, содержащий идентификатор заявки и новый статус.
     * @return ResponseEntity с HTTP-статусом 200 OK, если статус успешно обновлен.
     */
    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить статус заявки на стажировку",
            description = "Обновляет статус заявки на стажировку. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус заявки успешно изменен"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Идентификатор заявки и новый статус", required = true)
    public ResponseEntity<Void> changeApplicationStatus(@RequestBody ApplicationStatusDto statusDto) {
        applicationService.changeStatus(statusDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получение всех заявок на стажировку.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return ResponseEntity с списком объектов ApplicationDto и HTTP-статусом 200 OK,
     * или ResponseEntity с HTTP-статусом 204 NO CONTENT, если заявки не найдены.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все заявки на стажировку",
            description = "Возвращает список всех заявок на стажировку с указанным статусом (если он указан). " +
                    "Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заявок получен"),
            @ApiResponse(responseCode = "204", description = "Заявки не найдены"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameters({
            @Parameter(name = "status", description = "Статус заявки для получения заявок с определенным статусом"),
            @Parameter(name = "internshipId", description = "Идентификатор стажировки, на которую была оставлена заявка")
    })
    public ResponseEntity<List<ApplicationDto>> getAllApplications(@RequestParam(required = false) String status,
                                                                   @RequestParam(required = false) Long internshipId) {
        List<ApplicationDto> applications;
        if (status == null && internshipId == null) {
            applications = applicationService.getAll();
        } else if (status != null && internshipId == null) {
            applications = applicationService.getByStatus(status);
        } else if (status == null) {
            applications = applicationService.getAllByInternshipId(internshipId);
        } else {
            applications = applicationService.getAllByInternshipIdAndStatus(internshipId, status);
        }
        if (applications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    /**
     * Получение заявки на стажировку по идентификатору.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param id Идентификатор заявки для получения.
     * @return ResponseEntity с объектом ApplicationDto и HTTP-статусом 200 OK, если найдена,
     * или ResponseEntity с HTTP-статусом 404 NOT FOUND, если заявка не найдена.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить заявку по идентификатору",
            description = "Возвращает информацию о заявке с указанным идентификатором. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка найдена"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор заявки")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
        ApplicationDto application = applicationService.getById(id);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }
}
