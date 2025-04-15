package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.ApplicationEntity;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.InternshipEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.GetApplicationsRequest;
import org.example.internship.model.request.application.UpdateApplicationStatusRequest;
import org.example.internship.model.request.application.CreateApplicationRequest;
import org.example.internship.model.response.application.Application;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.service.application.ApplicationService;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.internship.InternshipService;
import org.example.internship.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


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
    private final InternshipService internshipService;
    private final AuditService auditService;

    private final Validator validator;
    private final Mapper mapper;

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
    public ResponseEntity<Application> createApplication(
            @RequestBody CreateApplicationRequest application) {
        InternshipEntity internship = internshipService.getInternshipById(application.getInternshipId());

        if (internship.getRegistrationEndDate().isBefore(LocalDate.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.APL_400.getCode(), "Registration for the internship is closed");
        }
        if (!validator.emailIsValid(application.getEmail())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.APL_400.getCode(), "Wrong e-mail format");
        }
        if (!validator.phoneNumberIsValid(application.getPhoneNumber())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.APL_400.getCode(), "Wrong phone number format");
        }

        ApplicationEntity applicationEntity = applicationService.saveApplication(application);
        auditService.addRecord(AuditEntityType.APPLICATION, AuditActionType.CREATE, applicationEntity.getId(), applicationEntity.getPhoneNumber(), null);
        return new ResponseEntity<>(mapper.map(applicationEntity, Application.class), HttpStatus.CREATED);
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
    public ResponseEntity<Application> changeApplicationStatus(@RequestBody UpdateApplicationStatusRequest statusDto,
                                                               @RequestHeader String username) {
        ApplicationEntity application = applicationService.changeApplicationStatus(statusDto);
        auditService.addRecord(AuditEntityType.APPLICATION, AuditActionType.UPDATE, application.getId(), null, username);
        return new ResponseEntity<>(mapper.map(application, Application.class), HttpStatus.OK);
    }

    /**
     * Получение всех заявок на стажировку.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return ResponseEntity с списком объектов ApplicationDto и HTTP-статусом 200 OK,
     * или ResponseEntity с HTTP-статусом 204 NO CONTENT, если заявки не найдены.
     */
    @PostMapping("/page")
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
    public ResponseEntity<Page<Application>> getApplications(@RequestBody GetApplicationsRequest request) {
        org.springframework.data.domain.Page<ApplicationEntity> page = applicationService.getApplications(request);

        Page<Application> result = Page.<Application>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Application.class))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
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
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        return new ResponseEntity<>(mapper.map(application, Application.class), HttpStatus.OK);
    }
}
