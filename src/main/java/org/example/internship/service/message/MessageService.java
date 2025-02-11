package org.example.internship.service.message;

import org.example.internship.model.request.CreateMessageRequest;
import org.example.internship.model.response.Message;

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
    void create(CreateMessageRequest message);

    /**
     * Получение списка сообщений, которые получил или отправил пользователь с указанным ID.
     *
     * @param id ID получателя или отправителя
     * @return список сообщений
     */
    List<Message> getByReceiverIdOrSenderId(Long id);
}
