package org.example.internship.controller;

import org.example.internship.dto.request.NewUserDto;
import org.example.internship.dto.response.UserDto;
import org.example.internship.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void create_returnCreated() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setUsername("test_user");

        ResponseEntity<Void> response = userController.create(newUserDto);

        verify(userService, times(1)).create(newUserDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getByParam_withUsername_returnUser() {
        String username = "test_user";
        UserDto userDto = new UserDto();
        userDto.setUsername(username);

        when(userService.getByUsername(username)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getByParam(username, null);

        verify(userService, times(1)).getByUsername(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void getByParam_withEmail_returnUser() {
        String email = "test@mail.com";
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        when(userService.getByEmail(email)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getByParam(null, email);

        verify(userService, times(1)).getByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void getByParam_withBothParams_returnBadRequest() {
        ResponseEntity<UserDto> response = userController.getByParam("username", "email");

        verify(userService, never()).getByUsername(anyString());
        verify(userService, never()).getByEmail(anyString());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getByParam_withNoParams_returnBadRequest() {
        ResponseEntity<UserDto> response = userController.getByParam(null, null);

        verify(userService, never()).getByUsername(anyString());
        verify(userService, never()).getByEmail(anyString());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAll_returnListOfUsers() {
        List<UserDto> users = List.of(new UserDto());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = userController.getAll();

        verify(userService, times(1)).getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void getAll_emptyList_returnNoContent() {
        when(userService.getAllUsers()).thenReturn(List.of());

        ResponseEntity<List<UserDto>> response = userController.getAll();

        verify(userService, times(1)).getAllUsers();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getById_returnUser() {
        Long id = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(id);

        when(userService.getById(id)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getById(id);

        verify(userService, times(1)).getById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void archiveUser_returnOk() {
        String username = "test_user";

        ResponseEntity<Void> response = userController.archiveUser(username);

        verify(userService, times(1)).archiveUser(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}