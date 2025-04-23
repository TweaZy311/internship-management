package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internship.entity.AuditEntity;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.CreateAuditRequest;
import org.example.internship.model.response.Audit;
import org.example.internship.service.audit.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Контроллер для управления аудит-логами.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
@Tag(name = "Аудит", description = "Эндпоинты для добавления и получения записей аудита")
public class AuditController {

    private final AuditService auditService;
    private final Mapper mapper;

    /**
     * Добавляет новую запись аудита.
     *
     * @param request объект с информацией для создания записи аудита
     */
    @Operation(summary = "Добавить запись аудита", description = "Добавляет новую запись в аудит-лог")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись успешно добавлена")
    })
    @PostMapping("")
    public void addAudit(
            @RequestBody(description = "Данные для создания записи аудита", required = true,
                    content = @Content(schema = @Schema(implementation = CreateAuditRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateAuditRequest request) {
        auditService.addRecord(
                request.getEntityType(),
                request.getActionType(),
                request.getEntityId(),
                request.getEntityName(),
                request.getUsername()
        );
    }

    /**
     * Получение списка записей аудита с пагинацией.
     *
     * @param request параметры пагинации и фильтрации
     * @return страница с результатами аудита
     */
    @Operation(summary = "Получить записи аудита с пагинацией", description = "Возвращает список записей аудита согласно фильтрам и пагинации")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/page")
    public ResponseEntity<Page<Audit>> getAudit(
            @RequestBody(description = "Параметры пагинации и фильтрации", required = true,
                    content = @Content(schema = @Schema(implementation = BaseGetListRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<AuditEntity> page = auditService.getPage(request);
        Page<Audit> result = Page.<Audit>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(mapper.mapAsList(page.getContent(), Audit.class))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Получение всех возможных типов сущностей аудита.
     *
     * @return список типов сущностей
     */
    @Operation(summary = "Получить типы сущностей для аудита", description = "Возвращает список всех доступных типов сущностей аудита")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Типы успешно получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditEntityType.class))))
    })
    @GetMapping("/entity-type")
    public ResponseEntity<List<AuditEntityType>> getEntityTypes() {
        return new ResponseEntity<>(Arrays.asList(AuditEntityType.values()), HttpStatus.OK);
    }
}
