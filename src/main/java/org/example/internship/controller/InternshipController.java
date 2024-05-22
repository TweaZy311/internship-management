package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.internship.InternshipStatusDto;
import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.ReportDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.exception.ExceptionResponse;
import org.example.internship.service.internship.InternshipService;
import org.example.internship.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с программами стажировок.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internship")
@Tag(name = "Управление стажировками")
public class InternshipController {
    private final InternshipService internshipService;
    private final Validator validator;

    /**
     * Создание новой программы стажировки.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param internship информация о новой программе стажировки
     * @return HTTP-ответ с кодом состояния 201 CREATED в случае успешного создания программы,
     * или соответствующий HTTP-ответ с кодом состояния 400 BAD REQUEST в случае неверного ввода данных
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую стажировку",
            description = "Создает новую стажировку. Проверяет корректность введенных дат. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Стажировка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректный ввод дат"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные новой стажировки", required = true)
    public ResponseEntity<ExceptionResponse> createInternship(@RequestBody NewInternshipDto internship) {
        if (!validator.dateIsValid(internship.getStartDate(),
                internship.getEndDate(),
                internship.getRegistrationEndDate())) {
            ExceptionResponse exceptionResponse = new ExceptionResponse("Wrong date input");
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
        }
        internshipService.save(internship);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Изменение статуса программы стажировки.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param statusDto объект с информацией о статусе программы стажировки, который нужно изменить
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного изменения статуса
     */
    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить статус стажировки",
            description = "Обновляет статус стажировки. Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Стажировка не найдена"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Идентификатор и статус стажировки", required = true)
    public ResponseEntity<Void> changeInternshipStatus(@RequestBody InternshipStatusDto statusDto) {
        internshipService.changeStatus(statusDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получение списка всех программ стажировок.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param status фильтр по статусу программы стажировки (необязательный)
     * @return HTTP-ответ со списком программ стажировок и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все стажировки",
            description = "Возвращает список всех стажировок c указанным статусом (если он указан). Доступно только администраторам.")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список стажировок"),
            @ApiResponse(responseCode = "204", description = "Список пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "status", description = "Статус стажировки")
    public ResponseEntity<List<AdminInternshipDto>> getAllInternships(@RequestParam(required = false) String status) {
        List<AdminInternshipDto> internships;
        if (status != null) {
            internships = internshipService.getByStatus(status);
        } else {
            internships = internshipService.getAll();
        }
        if (internships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(internships, HttpStatus.OK);
    }

    /**
     * Получение списка открытых программ стажировок.
     *
     * @return HTTP-ответ со списком открытых программ стажировок и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/opened")
    @Operation(summary = "Получить все открытые стажировки",
            description = "Возвращает список всех открытых стажировок.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список открытых стажировок"),
            @ApiResponse(responseCode = "204", description = "Список пуст"),
    })
    public ResponseEntity<List<PublicInternshipDto>> getAllOpenedInternships() {
        List<PublicInternshipDto> internships = internshipService.getOpened();
        if (internships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(internships, HttpStatus.OK);
    }

    /**
     * Получение информации о программе стажировки по ее идентификатору.
     *
     * @param id идентификатор программы стажировки
     * @return HTTP-ответ с информацией о программе стажировки и кодом состояния 200 OK в случае успешного получения данных
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить стажировку по идентификатору",
            description = "Возвращает информацию о стажировке с указанным идентификатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список открытых стажировок"),
            @ApiResponse(responseCode = "404", description = "Стажировка не найдена")
    })
    @Parameter(name = "id", description = "Идентификатор стажировки", required = true)
    public ResponseEntity<PublicInternshipDto> getInternshipById(@PathVariable Long id) {
        PublicInternshipDto internship = internshipService.getById(id);
        return new ResponseEntity<>(internship, HttpStatus.OK);
    }


    /**
     * Обновление информации о программе стажировки.
     *
     * @param dto объект, содержащий обновленную информацию о стажировке
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного изменения статуса,
     * или соответствующий HTTP-ответ с кодом состояния 400 BAD REQUEST в случае неверного ввода данных
     */
    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить стажировку по идентификатору",
            description = "Обновляет информацию о стажировке. Доступно только администраторам")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Стажировка успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректный ввод дат"),
            @ApiResponse(responseCode = "404", description = "Стажировка не найдена"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Информация об обновленной стажировке", required = true)
    public ResponseEntity<ExceptionResponse> updateInternship(@RequestBody UpdateInternshipDto dto) {
        if (!validator.dateIsValid(dto.getStartDate(), dto.getEndDate(),
                dto.getRegistrationStartDate(), dto.getRegistrationEndDate())) {
            return new ResponseEntity<>(new ExceptionResponse("Wrong date input"), HttpStatus.BAD_REQUEST);
        }
        internshipService.update(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Генерация ведомости о программе стажировки.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param id идентификатор программы стажировки
     * @return HTTP-ответ с ведомостью стажировки и кодом состояния 200 OK в случае успешной генерации ведомости
     */
    @GetMapping("/{id}/report")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить ведомость стажировки",
            description = "Формирует ведомость об успеваемости участников стажировки. Доступно только администраторам")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ведомость сформирована"),
            @ApiResponse(responseCode = "404", description = "Стажировка не найдена"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор стажировки", required = true)
    public ResponseEntity<List<ReportDto>> getReport(@PathVariable Long id) {
        List<ReportDto> report = internshipService.createReport(id);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
