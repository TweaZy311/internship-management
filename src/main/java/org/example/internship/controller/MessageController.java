package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.internship.annotation.UserIdMatches;
import org.example.internship.dto.request.NewMessageDto;
import org.example.internship.dto.response.MessageDto;
import org.example.internship.service.message.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с сообщениями.
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Управление сообщениями")
public class MessageController {
    private final MessageService messageService;

    /**
     * Отправка сообщения.
     * Доступно только пользователям с ролью USER или ADMIN.
     *
     * @param message данные нового сообщения.
     * @return ResponseEntity с HTTP-статусом 201 CREATED, если сообщение успешно отправлено.
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Отправить сообщение",
            description = "Отправляет сообщение другому пользователю. Доступно только зарегистрированным пользователям.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Сообщение успешно отправлено"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные нового сообщения", required = true)
    public ResponseEntity<Void> sendMessage(@RequestBody NewMessageDto message) {
        messageService.create(message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Получение списка сообщений, отправленных и полученных пользователем.
     * Доступно только для пользователей с ролью USER или ADMIN.
     * Дополнительно проверяет, что ID пользователя в запросе совпадает с ID пользователя,
     * отправляющего запрос.
     *
     * @param id ID получателя или отправителя
     * @return ResponseEntity с HTTP-статусом 200 OK,
     * или ResponseEntity с HTTP-статусом 204 NO CONTENT, если сообщения не найдены.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @UserIdMatches
    @Operation(summary = "Получить список сообщений",
            description = "Возвращает список сообщений, отправленных и полученных пользователем. " +
                    "Доступно только для авторизованного пользователя, чей ID совпадает с запрашиваемым.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список сообщений успешно получен"),
            @ApiResponse(responseCode = "204", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет нужных прав")
    })
    @Parameter(name = "id", description = "ID пользователя (отправителя или получателя)", required = true)
    public ResponseEntity<List<MessageDto>> getMessagesById(@RequestParam Long id){
        List<MessageDto> messages = messageService.getByReceiverIdOrSenderId(id);
        if (messages.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
