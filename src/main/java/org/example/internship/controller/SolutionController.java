package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.annotation.GitlabTokenRequired;
import org.example.internship.dto.request.solution.SolutionStatusDto;
import org.example.internship.dto.response.solution.SolutionDto;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<Void> addSolution(@RequestBody PushSystemHookEvent request) {
        if (gitlabService.isForkedRepository(request.getProjectId())) {
            solutionService.add(request);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Обновление статуса решения задания.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param dto информация для обновления статуса решения
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
    public ResponseEntity<Void> updateSolutionStatus(@RequestBody SolutionStatusDto dto) {
        solutionService.updateStatus(dto);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<SolutionDto> getSolutionById(@PathVariable Long id) {
        SolutionDto solution = solutionService.getById(id);
        return new ResponseEntity<>(solution, HttpStatus.OK);
    }

    /**
     * Получение списка всех решений заданий.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param status статус решения (необязательный параметр)
     * @param taskId идентификатор задания, которому соответствуют решения (необязательный параметр)
     * @return HTTP-ответ со списком всех решений заданий и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/all")
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
    @Parameters({
            @Parameter(name = "status", description = "Статус решения"),
            @Parameter(name = "taskId", description = "Идентификатор задания, которому соответствуют решения")
    })
    public ResponseEntity<List<SolutionDto>> getAllSolutions(@RequestParam(required = false) String status,
                                                             @RequestParam(required = false) Long taskId) {
        if (status != null && taskId != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<SolutionDto> solutions;
        if (status != null) {
            solutions = solutionService.getAllByStatus(status);
        } else if (taskId != null) {
            solutions = solutionService.getAllByTaskId(taskId);
        } else {
            solutions = solutionService.getAll();
        }
        if (solutions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(solutions, HttpStatus.OK);
    }

}
