package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.entity.*;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.*;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.model.response.Report;
import org.example.internship.model.response.User;
import org.example.internship.model.response.internship.Internship;
import org.example.internship.model.response.internship.PrivateInternshipInfo;
import org.example.internship.model.response.internship.PublicInternshipInfo;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.internship.InternshipService;
import org.example.internship.service.user.UserService;
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
    private final AuditService auditService;
    private final UserService userService;

    private final Validator validator;
    private final Mapper mapper;

    /**
     * Создание новой программы стажировки.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request информация о новой программе стажировки
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
    public ResponseEntity<Internship> createInternship(@RequestBody CreateUpdateInternshipRequest request,
                                                       @RequestHeader("username") String username) {
        //todo add validation to registration start date
        if (!validator.dateIsValid(request.getStartDate(),
                request.getEndDate(),
                request.getRegistrationEndDate())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong date input");
        }
        InternshipEntity internship = internshipService.saveInternship(request);
        auditService.addRecord(AuditEntityType.INTERNSHIP, AuditActionType.CREATE, internship.getId(), internship.getName(), username);
        return new ResponseEntity<>(mapper.map(internship, Internship.class), HttpStatus.CREATED);
    }

//    /**
//     * Изменение статуса программы стажировки.
//     * Доступно только пользователям с ролью ADMIN.
//     *
//     * @param statusDto объект с информацией о статусе программы стажировки, который нужно изменить
//     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного изменения статуса
//     */
//    @PatchMapping("/status")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Изменить статус стажировки",
//            description = "Обновляет статус стажировки. Доступно только администраторам.")
//    @SecurityRequirement(name = "basicAuth")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Статус успешно изменен"),
//            @ApiResponse(responseCode = "404", description = "Стажировка не найдена"),
//            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
//    })
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Идентификатор и статус стажировки", required = true)
//    public ResponseEntity<Void> changeInternshipStatus(@RequestBody UpdateInternshipStatusRequest statusDto) {
//        internshipService.changeStatus(statusDto);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    /**
     * Получение списка всех программ стажировок.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request фильтр по статусу программы стажировки (необязательный)
     * @return HTTP-ответ со списком программ стажировок и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @PostMapping("/page")
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
    public ResponseEntity<Page<Internship>> getInternships(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<InternshipEntity> page = internshipService.getInternships(request);

        //TODO FIXME
        if (false) { //если пользователь не админ то возвращем только открытые
            request.getFilters()
                    .add(new SearchCriteria(SearchKey.IS_PUBLISHED, SearchOperation.EQ, Boolean.TRUE, BooleanOperator.AND));
        }

        Page<Internship> result = Page.<Internship>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Internship.class))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
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
    //todo deprecated
    public ResponseEntity<List<PublicInternshipInfo>> getAllOpenedInternships() {
        List<InternshipEntity> internships = internshipService.getInternshipsByIsOpen(true);
        return new ResponseEntity<>(mapper.mapAsList(internships, PublicInternshipInfo.class), HttpStatus.OK);
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
    @PreAuthorize("@securityService.hasAccess(#isPrivate)")
    public ResponseEntity<Internship> getInternshipById(@PathVariable Long id,
                                                        @RequestParam(name = "private", defaultValue = "false") Boolean isPrivate) {
        InternshipEntity internship = internshipService.getInternshipById(id);
        if (isPrivate) {
            List<UserEntity> participants = userService.getUsersByInternshipIdAndRole(id, UserRole.USER);
            PrivateInternshipInfo internshipInfo = mapper.map(internship, PrivateInternshipInfo.class);
            internshipInfo.setParticipants(mapper.mapAsList(participants, User.class));
            return new ResponseEntity<>(internshipInfo, HttpStatus.OK);
        }
        return new ResponseEntity<>(mapper.map(internship, PublicInternshipInfo.class), HttpStatus.OK);
    }


    /**
     * Обновление информации о программе стажировки.
     *
     * @param request объект, содержащий обновленную информацию о стажировке
     * @return HTTP-ответ с кодом состояния 200 OK в случае успешного изменения статуса,
     * или соответствующий HTTP-ответ с кодом состояния 400 BAD REQUEST в случае неверного ввода данных
     */
    @PatchMapping("/update/{id}")
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
    public ResponseEntity<Internship> updateInternship(@PathVariable Long id,
                                                       @RequestBody CreateUpdateInternshipRequest request,
                                                       @RequestHeader("username") String username) {
        //todo add validation to registration start date
        if (!validator.dateIsValid(request.getStartDate(), request.getEndDate(),
                request.getRegistrationStartDate(), request.getRegistrationEndDate())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong date input");
        }
        InternshipEntity internship = internshipService.updateInternship(id, request);
        auditService.addRecord(AuditEntityType.INTERNSHIP, AuditActionType.UPDATE, internship.getId(), internship.getName(), username);
        return new ResponseEntity<>(mapper.map(internship, Internship.class), HttpStatus.OK);
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
    public ResponseEntity<List<Report>> getReport(@PathVariable Long id) {
        List<Report> report = internshipService.createReport(id);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
