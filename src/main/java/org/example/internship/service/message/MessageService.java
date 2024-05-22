package org.example.internship.service.message;

import org.example.internship.dto.request.NewMessageDto;
import org.example.internship.dto.response.MessageDto;

import java.util.List;

/**
 * Сервис для работы с сообщениями.
 */
public interface MessageService {

    /**
     * Создание нового сообщения.
     *
     * @param message данные нового сообщения
     */
    void create(NewMessageDto message);

    /**
     * Получение списка сообщений, которые получил или отправил пользователь с указанным ID.
     *
     * @param id ID получателя или отправителя
     * @return список сообщений
     */
    List<MessageDto> getByReceiverIdOrSenderId(Long id);
}
