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
import org.example.internship.annotation.GitlabTokenRequired;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.SolutionEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.solution.UpdateSolutionStatusRequest;
import org.example.internship.model.response.solution.Solution;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с решениями заданий.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/solution")
@Tag(name = "Управление решениями заданий", description = "Операции по управлению решениями заданий пользователей")
public class SolutionController {
    private final SolutionService solutionService;
    private final GitlabService gitlabService;
    private final AuditService auditService;
    private final Mapper mapper;

    /**
     * Добавление нового решения задания.
     * Доступно только пользователям Gitlab, у которых имеется токен.
     *
     * @param request информация о push-событии из GitLab
     * @return HTTP-ответ с кодом состояния 201 CREATED и DTO добавленного решения
     */
    @PostMapping("/add")
    @GitlabTokenRequired
    @Operation(
            summary = "Добавить новое решение задания",
            description = "Добавляет новое решение на основе пуша в GitLab. Доступно только пользователям с валидным GitLab-токеном."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Решение успешно добавлено",
                    content = @Content(schema = @Schema(implementation = Solution.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа (отсутствует GitLab токен)")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Событие Push из GitLab", required = true,
            content = @Content(schema = @Schema(implementation = PushSystemHookEvent.class)))
    public ResponseEntity<Solution> addSolution(@RequestBody PushSystemHookEvent request) {
        SolutionEntity solution = null;
        if (gitlabService.isForkedRepository(request.getProjectId())) {
            solution = solutionService.addSolution(request);
        }
        return new ResponseEntity<>(mapper.map(solution, Solution.class), HttpStatus.CREATED);
    }

    /**
     * Обновление статуса решения задания.
     * Доступно только администраторам.
     *
     * @param request  данные для обновления статуса
     * @param username имя пользователя, производящего обновление (для аудита)
     * @return HTTP-ответ с обновлённым решением и кодом 200 OK
     */
    @PatchMapping("/set-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить статус решения задания",
            description = "Обновляет статус конкретного решения. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлён",
                    content = @Content(schema = @Schema(implementation = Solution.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа (требуется роль ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Решение не найдено")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация для обновления статуса решения", required = true,
            content = @Content(schema = @Schema(implementation = UpdateSolutionStatusRequest.class)))
    public ResponseEntity<Solution> updateSolutionStatus(
            @RequestBody UpdateSolutionStatusRequest request,
            @Parameter(description = "Имя пользователя, совершающего действие", required = true)
            @RequestHeader("username") String username) {
        SolutionEntity solution = solutionService.updateSolutionStatus(request);
        auditService.addRecord(AuditEntityType.SOLUTION, AuditActionType.UPDATE, solution.getId(), solution.getRepositoryUrl(), username);
        return new ResponseEntity<>(mapper.map(solution, Solution.class), HttpStatus.OK);
    }

    /**
     * Получение информации о решении задания по его ID.
     * Доступно только администраторам.
     *
     * @param id идентификатор решения
     * @return HTTP-ответ с DTO решения и кодом 200 OK
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить решение по ID",
            description = "Возвращает решение по идентификатору. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Решение успешно найдено",
                    content = @Content(schema = @Schema(implementation = Solution.class))),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Решение не найдено")
    })
    public ResponseEntity<Solution> getSolutionById(
            @Parameter(description = "ID решения", required = true)
            @PathVariable Long id) {
        SolutionEntity solution = solutionService.getSolutionById(id);
        return new ResponseEntity<>(mapper.map(solution, Solution.class), HttpStatus.OK);
    }

    /**
     * Получение списка решений заданий с пагинацией.
     * Доступно только администраторам.
     *
     * @param request параметры пагинации и фильтрации
     * @return HTTP-ответ с пагинированным списком решений и кодом 200 OK или 204 NO CONTENT
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить список решений заданий (с пагинацией)",
            description = "Возвращает список решений с поддержкой пагинации. Доступно только администраторам.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Решения найдены",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметры запроса списка решений", required = true,
            content = @Content(schema = @Schema(implementation = BaseGetListRequest.class)))
    public ResponseEntity<Page<Solution>> getSolutions(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<SolutionEntity> page = solutionService.getSolutions(request);

        Page<Solution> result = Page.<Solution>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Solution.class))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
