package org.example.internship.controller;

import org.example.internship.dto.request.NewMessageDto;
import org.example.internship.dto.response.MessageDto;
import org.example.internship.service.message.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;


    @Test
    void sendMessage_returnCreated() {
        NewMessageDto messageDto = new NewMessageDto();
        messageDto.setText("Test Message");
        messageDto.setSenderId(1L);
        messageDto.setReceiverId(2L);

        ResponseEntity<Void> response = messageController.sendMessage(messageDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(messageService, times(1)).create(messageDto);
    }

    @Test
    void getMessagesById_returnListOfMessages() {
        MessageDto messageDto = new MessageDto();
        messageDto.setText("Test Message");
        messageDto.setSenderId(1L);
        messageDto.setReceiverId(2L);
        messageDto.setSentAt(LocalDateTime.now());

        when(messageService.getByReceiverIdOrSenderId(1L)).thenReturn(List.of(messageDto));

        ResponseEntity<List<MessageDto>> response = messageController.getMessagesById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(messageDto, response.getBody().get(0));
    }

    @Test
    void getMessagesById_emptyList_returnNoContent() {
        when(messageService.getByReceiverIdOrSenderId(1L)).thenReturn(List.of());

        ResponseEntity<List<MessageDto>> response = messageController.getMessagesById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}