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
 * Контроллер для управления заявками на стажировку.
 * Предоставляет возможности создания, обновления статуса и получения информации о заявках.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
@Tag(name = "Управление заявками", description = "Операции по созданию, получению и обновлению заявок на стажировку")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final InternshipService internshipService;
    private final AuditService auditService;
    private final Validator validator;
    private final Mapper mapper;

    /**
     * Создать новую заявку на стажировку.
     *
     * @param application Данные новой заявки.
     * @return Созданная заявка.
     */
    @PostMapping("/create")
    @Operation(summary = "Создать заявку",
            description = "Создает новую заявку на стажировку. Проверяет формат электронной почты и телефона.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заявка успешно создана", content = @Content(schema = @Schema(implementation = Application.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public ResponseEntity<Application> createApplication(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные новой заявки", required = true,
                    content = @Content(schema = @Schema(implementation = CreateApplicationRequest.class)))
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
     * Обновить статус заявки на стажировку (только для ADMIN).
     *
     * @param statusDto Объект с ID заявки и новым статусом.
     * @param username  Имя пользователя, выполнившего операцию.
     * @return Обновлённая заявка.
     */
    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус заявки", description = "Изменяет статус заявки. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлён", content = @Content(schema = @Schema(implementation = Application.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<Application> changeApplicationStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новый статус заявки", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateApplicationStatusRequest.class)))
            @RequestBody UpdateApplicationStatusRequest statusDto,
            @Parameter(description = "Имя администратора") @RequestHeader String username) {
        ApplicationEntity application = applicationService.changeApplicationStatus(statusDto);
        auditService.addRecord(AuditEntityType.APPLICATION, AuditActionType.UPDATE, application.getId(), null, username);
        return new ResponseEntity<>(mapper.map(application, Application.class), HttpStatus.OK);
    }

    /**
     * Получить список заявок (только для ADMIN).
     *
     * @param request Параметры фильтрации и пагинации.
     * @return Список заявок.
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список заявок", description = "Получает список заявок по параметрам. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список найден", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<Page<Application>> getApplications(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметры запроса", required = true,
                    content = @Content(schema = @Schema(implementation = GetApplicationsRequest.class)))
            @RequestBody GetApplicationsRequest request) {
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
     * Получить заявку по идентификатору (только для ADMIN).
     *
     * @param id Идентификатор заявки.
     * @return Заявка.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить заявку по ID", description = "Возвращает заявку по её идентификатору. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявка найдена", content = @Content(schema = @Schema(implementation = Application.class))),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<Application> getApplicationById(
            @Parameter(description = "ID заявки") @PathVariable Long id) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        return new ResponseEntity<>(mapper.map(application, Application.class), HttpStatus.OK);
    }
}
