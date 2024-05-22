package org.example.internship.service.impl;

import org.example.internship.dto.request.NewMessageDto;
import org.example.internship.dto.response.MessageDto;
import org.example.internship.mapper.MessageMapper;
import org.example.internship.model.Message;
import org.example.internship.model.user.User;
import org.example.internship.repository.MessageRepository;
import org.example.internship.service.message.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private NewMessageDto newMessageDto;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        User sender = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        newMessageDto = new NewMessageDto();
        newMessageDto.setText("Test Message");
        newMessageDto.setSenderId(1L);
        newMessageDto.setReceiverId(2L);

        message = Message.builder()
                .id(1L)
                .text("Test Message")
                .sentAt(LocalDateTime.now())
                .sender(sender)
                .receiver(receiver)
                .build();

        messageDto = new MessageDto();
        messageDto.setText("Test Message");
        messageDto.setSenderId(1L);
        messageDto.setReceiverId(2L);
        messageDto.setSentAt(message.getSentAt());
    }

    @Test
    void create_createNewMessage() {
        when(messageMapper.newDtoToModel(newMessageDto)).thenReturn(message);

        messageService.create(newMessageDto);

        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void getByReceiverIdOrSenderId_returnListOfMessages() {
        when(messageRepository.findBySenderIdOrReceiverId(1L)).thenReturn(List.of(message));
        when(messageMapper.modelToDto(message)).thenReturn(messageDto);

        List<MessageDto> result = messageService.getByReceiverIdOrSenderId(1L);

        assertEquals(1, result.size());
        assertEquals(messageDto, result.get(0));
    }
}