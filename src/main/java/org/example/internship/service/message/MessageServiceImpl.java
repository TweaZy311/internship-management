package org.example.internship.service.message;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.UserEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.CreateMessageRequest;
import org.example.internship.model.response.Message;
import org.example.internship.entity.MessageEntity;
import org.example.internship.repository.MessageRepository;
import org.example.internship.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Реализация сервиса для работы с сообщениями.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * {@inheritDoc}
     *
     * @param message данные нового сообщения
     */
    @Override
    public void create(CreateMessageRequest message) {
        MessageEntity newMessageEntity = mapper.map(message, MessageEntity.class);
        UserEntity reciever = userRepository.findById(message.getReceiverId())
                        .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), "User with such ID could not be found"));
        UserEntity sender = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), "User with such ID could not be found"));
        newMessageEntity.setSender(sender);
        newMessageEntity.setReceiver(reciever);

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
