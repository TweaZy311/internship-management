package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.StatusEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Page;
import org.example.internship.model.Status;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.status.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
@Tag(name = "Управление статусами")
public class StatusController {
    private final StatusService statusService;
    private final AuditService auditService;
    private final Mapper mapper;

    @Operation(summary = "Получить статус по ID", description = "Требуется роль ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус найден", content = @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> getStatusById(
            @Parameter(description = "ID статуса") @PathVariable Long id) {
        StatusEntity status = statusService.getStatusById(id);
        return new ResponseEntity<>(mapper.map(status, Status.class), HttpStatus.OK);
    }

    @Operation(summary = "Получить список статусов (с пагинацией)", description = "Требуется роль ADMIN")
    @ApiResponse(responseCode = "200", description = "Список статусов",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Status>> getStatuses(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметры фильтрации и пагинации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BaseGetListRequest.class)))
            @RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<StatusEntity> page = statusService.getStatuses(request);

        Page<Status> result = Page.<Status>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Status.class))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Создать новый статус", description = "Требуется роль ADMIN")
    @ApiResponse(responseCode = "201", description = "Статус создан",
            content = @Content(schema = @Schema(implementation = Status.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> createStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для создания статуса",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUpdateStatusRequest.class)))
            @RequestBody CreateUpdateStatusRequest request,
            @Parameter(description = "Имя пользователя") @RequestHeader("username") String username) {
        StatusEntity status = statusService.createStatus(request);
        auditService.addRecord(AuditEntityType.STATUS, AuditActionType.CREATE, status.getId(), status.getName(), username);
        return new ResponseEntity<>(mapper.map(status, Status.class), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить статус", description = "Требуется роль ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус обновлён",
                    content = @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> updateStatus(
            @Parameter(description = "ID статуса") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для обновления статуса",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUpdateStatusRequest.class)))
            @RequestBody CreateUpdateStatusRequest request,
            @Parameter(description = "Имя пользователя") @RequestHeader("username") String username) {
        StatusEntity status = statusService.updateStatus(id, request);
        auditService.addRecord(AuditEntityType.STATUS, AuditActionType.UPDATE, status.getId(), status.getName(), username);
        return new ResponseEntity<>(mapper.map(status, Status.class), HttpStatus.OK);
    }
}