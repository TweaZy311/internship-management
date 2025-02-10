package org.example.internship.service.message;

import lombok.RequiredArgsConstructor;
import org.example.internship.model.request.NewMessageDto;
import org.example.internship.model.response.MessageDto;
import org.example.internship.entity.MessageEntity;
import org.example.internship.mapper.MessageMapper;
import org.example.internship.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с сообщениями.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    /**
     * {@inheritDoc}
     *
     * @param message данные нового сообщения
     */
    @Override
    public void create(NewMessageDto message) {
        MessageEntity newMessageEntity = messageMapper.newDtoToModel(message);
        messageRepository.save(newMessageEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param id ID получателя или отправителя
     * @return список сообщений
     */
    @Override
    public List<MessageDto> getByReceiverIdOrSenderId(Long id) {
        List<MessageEntity> messageEntities = messageRepository.findBySenderIdOrReceiverId(id);
        return messageEntities.stream()
                .map(messageMapper::modelToDto)
                .collect(Collectors.toList());
    }
}
