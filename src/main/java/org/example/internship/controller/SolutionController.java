package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Управление решениями заданий")
public class SolutionController {
    private final SolutionService solutionService;
    private final GitlabService gitlabService;
    private final AuditService auditService;

    private final Mapper mapper;

    /**
     * Добавление нового решения задания.
     * Доступно только пользователям Gitlab, у которых имеется токен
     *
     * @param request информация о пуше в репозиторий
     * @return HTTP-ответ с кодом состояния 201 CREATED в случае успешного добавления решения
     */
    @PostMapping("/add")
    @GitlabTokenRequired
    @Operation(summary = "Добавить новое решение задания",
            description = "Добавляет новое решение задания. Доступно только пользователям GitLab с токеном.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Решение успешно добавлено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация о пуше в репозиторий", required = true)
    public ResponseEntity<Solution> addSolution(@RequestBody PushSystemHookEvent request) {
        Solution solution = null;
        if (gitlabService.isForkedRepository(request.getProjectId())) {
            solution = solutionService.addSolution(request);
        }
        //todo придумать как передавать заголовок
        return new ResponseEntity<>(solution, HttpStatus.CREATED);
    }

    /**
     * Обновление статуса решения задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request информация для обновления статуса решения
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного обновления статуса решения
     */
    @PatchMapping("/set-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус решения задания",
            description = "Обновляет статус решения задания. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус решения успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Указанное решение не найдено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация для обновления статуса решения", required = true)
    public ResponseEntity<Solution> updateSolutionStatus(@RequestBody UpdateSolutionStatusRequest request,
                                                         @RequestHeader("username") String username) {
        Solution solution = solutionService.updateSolutionStatus(request);
        //todo repository url == name??
        auditService.addRecord(AuditEntityType.SOLUTION, AuditActionType.UPDATE, solution.getId(), solution.getRepositoryUrl(), username);
        return new ResponseEntity<>(solution, HttpStatus.OK);
    }

    /**
     * Получение информации о решении задания по его идентификатору.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param id идентификатор решения задания
     * @return HTTP-ответ с информацией о решении задания и кодом состояния 200 OK в случае успешного получения данных
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить информацию о решении задания",
            description = "Возвращает информацию о решении задания по его идентификатору. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о решении успешно получена"),
            @ApiResponse(responseCode = "404", description = "Решение не найдено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<Solution> getSolutionById(@PathVariable Long id) {
        Solution solution = solutionService.getSolutionById(id);
        return new ResponseEntity<>(solution, HttpStatus.OK);
    }

    /**
     * Получение списка всех решений заданий.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return HTTP-ответ со списком всех решений заданий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список решений заданий",
            description = "Возвращает список всех решений заданий. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список решений успешно получен"),
            @ApiResponse(responseCode = "204", description = "Решения не найдены"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (указаны оба параметра одновременно)"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
//    @Parameters({
//            @Parameter(name = "status", description = "Статус решения"),
//            @Parameter(name = "taskId", description = "Идентификатор задания, которому соответствуют решения")
//    })
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
