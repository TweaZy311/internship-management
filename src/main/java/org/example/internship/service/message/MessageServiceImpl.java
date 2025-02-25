package org.example.internship.service.message;

import lombok.RequiredArgsConstructor;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.CreateMessageRequest;
import org.example.internship.model.response.Message;
import org.example.internship.entity.MessageEntity;
import org.example.internship.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для работы с сообщениями.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param message данные нового сообщения
     */
    @Override
    public void create(CreateMessageRequest message) {
        MessageEntity newMessageEntity = mapper.map(message, MessageEntity.class);
        messageRepository.save(newMessageEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param id ID получателя или отправителя
     * @return список сообщений
     */
    @Override
    public List<Message> getByReceiverIdOrSenderId(Long id) {
        List<MessageEntity> messageEntities = messageRepository.findBySenderIdOrReceiverId(id);
        return mapper.mapAsList(messageEntities, Message.class);
    }
}
