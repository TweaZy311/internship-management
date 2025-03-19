package org.example.internship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.annotation.UsernameMatches;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.response.User;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Контроллер для работы с пользователями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление пользователями")
public class UserController {
    private final UserService userService;
    private final GitlabService gitlabService;

    /**
     * Создание нового пользователя.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param request данные нового пользователя
     * @return HTTP-ответ с кодом состояния 201 CREATED в случае успешного создания пользователя
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать нового пользователя",
            description = "Создает нового пользователя. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким username или email уже существует"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового пользователя", required = true)
    public ResponseEntity<User> create(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        gitlabService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Получение информации о пользователе по параметрам.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @param username имя пользователя (опционально)
     * @param email    адрес электронной почты пользователя (опционально)
     * @return HTTP-ответ с информацией о пользователе и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 400 BAD REQUEST, если переданы оба параметра или ни одного из них
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить информацию о пользователе",
            description = "Возвращает информацию о пользователе по имени пользователя или адресу электронной почты. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (указаны оба параметра одновременно или не указан ни один)"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameters({
            @Parameter(name = "username", description = "Имя пользователя"),
            @Parameter(name = "email", description = "Email пользователя")
    })
    public ResponseEntity<User> getByParam(@RequestParam(required = false) String username,
                                           @RequestParam(required = false) String email) {
        if (username != null && email != null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong param input");
        }
        User user;
        if (username != null) {
            user = userService.getByUsername(username);
        } else if (email != null) {
            user = userService.getByEmail(email);
        } else {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong param input");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Получение списка всех пользователей.
     * Доступно только пользователям с ролью ADMIN.
     *
     * @return HTTP-ответ со списком всех пользователей и кодом состояния 200 OK в случае успешного получения данных,
     * или кодом состояния 204 NO CONTENT, если список пуст
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех пользователей",
            description = "Возвращает список всех пользователей. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "204", description = "Список пользователей пуст"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")

    })
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Получение пользователя по его идентификатору.
     * Доступно пользователям с ролью ADMIN или USER.
     *
     * @param id идентификатор пользователя
     * @return HTTP-ответ с данными пользователя и кодом состояния 200 OK в случае успешного получения данных
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию о пользователе по ID",
            description = "Возвращает информацию о пользователе по его идентификатору. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор пользователя", required = true)
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    /**
     * Отчисление и архивирование данных о пользователе по его username.
     * Доступно пользователям с ролью ADMIN или USER.
     *
     * @param username имя пользователя
     * @return HTTP-ответ с данными пользователя и кодом состояния 200 OK в случае успешного архивирования
     */
    @PatchMapping("/drop-out")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @UsernameMatches
    @Operation(summary = "Отчислить пользователя и поместить данные о нем в архив",
            description = "Помещает в архив данные о пользователе и его успеваемости. Доступно администраторам " +
                    "и пользователям, чей username совпадает с указанным в параметре запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь занесен в архив"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "username", description = "Имя пользователя", required = true)
    public ResponseEntity<Void> archiveUser(@RequestParam String username) {
        userService.archiveUser(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
