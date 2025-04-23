package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.annotation.UsernameMatches;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.entity.UserEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.request.GetUsersRequest;
import org.example.internship.model.response.User;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.service.audit.AuditService;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление пользователями")
public class UserController {
    private final UserService userService;
    private final GitlabService gitlabService;
    private final AuditService auditService;

    private final Mapper mapper;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким username или email уже существует"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового пользователя", required = true)
    public ResponseEntity<User> create(@RequestBody CreateUserRequest request,
                                       @RequestHeader("username") String username) {
        UserEntity user = userService.createUser(request);
        gitlabService.createUser(request);
        auditService.addRecord(AuditEntityType.USER, AuditActionType.CREATE, user.getId(), user.getUsername(), username);
        return new ResponseEntity<>(mapper.map(user, User.class), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить информацию о пользователе", description = "Возвращает информацию о пользователе по имени пользователя или адресу электронной почты. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (указаны оба параметра одновременно или не указан ни один)"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameters({
            @Parameter(name = "username", description = "Имя пользователя", required = false),
            @Parameter(name = "email", description = "Email пользователя", required = false)
    })
    public ResponseEntity<User> getByParam(@RequestParam(required = false) String username,
                                           @RequestParam(required = false) String email) {
        if (username != null && email != null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong param input");
        }
        UserEntity user;
        if (username != null) {
            user = userService.getByUsername(username);
        } else if (email != null) {
            user = userService.getByEmail(email);
        } else {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.ITS_400.getCode(), "Wrong param input");
        }
        return new ResponseEntity<>(mapper.map(user, User.class), HttpStatus.OK);
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех пользователей", description = "Возвращает список всех пользователей. Доступно только администраторам.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    public ResponseEntity<Page<User>> getUsers(@RequestBody GetUsersRequest request) {
        org.springframework.data.domain.Page<UserEntity> page = userService.getUsers(request);

        Page<User> result = Page.<User>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(mapper.mapAsList(page.getContent(), User.class))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить информацию о пользователе по ID", description = "Возвращает информацию о пользователе по его идентификатору. Доступно администраторам и пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "Идентификатор пользователя", required = true)
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @PatchMapping("/drop-out")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @UsernameMatches
    @Operation(summary = "Отчислить пользователя и поместить данные о нем в архив", description = "Помещает в архив данные о пользователе и его успеваемости. Доступно администраторам и пользователям, чей username совпадает с указанным в параметре запроса.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь занесен в архив"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "username", description = "Имя пользователя", required = true)
    public ResponseEntity<Void> archiveUser(@RequestParam String username,
                                            @RequestHeader("username") String headerUsername) {
        UserEntity user = userService.archiveUser(username);
        auditService.addRecord(AuditEntityType.USER, AuditActionType.UPDATE, user.getId(), user.getUsername(), headerUsername);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить роли текущего пользователя", description = "Возвращает список ролей текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роли текущего пользователя успешно получены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизованный пользователь")
    })
    public ResponseEntity<List<String>> getCurrentUserRoles() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
