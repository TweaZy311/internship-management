package org.example.internship.service.impl;

import org.example.internship.dto.request.NewUserDto;
import org.example.internship.dto.response.UserDto;
import org.example.internship.mapper.UserMapper;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.example.internship.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SolutionService solutionService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private GitlabService gitlabService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private NewUserDto newUserDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test-user")
                .email("test@test.com")
                .name("Test User")
                .password("test-password")
                .role(Role.USER)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("test-user");
        userDto.setEmail("test@test.com");
        userDto.setName("Test User");

        newUserDto = new NewUserDto();
        newUserDto.setUsername("new-user");
        newUserDto.setEmail("new@test.com");
        newUserDto.setName("New User");
        newUserDto.setInternshipId(1L);
    }

    @Test
    void getByEmail_returnUserWithEmail() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        UserDto result = userService.getByEmail("test@test.com");

        assertEquals(userDto, result);
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void getByEmail_userNotFound_throwException() {
        when(userRepository.findByEmail("nonexisting@test.com")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.getByEmail("nonexisting@test.com"));
    }

    @Test
    void getByUsername_returnUserWithUsername() {
        when(userRepository.findByUsername("test-user")).thenReturn(user);
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        UserDto result = userService.getByUsername("test-user");

        assertEquals(userDto, result);
        assertEquals(userDto.getUsername(), result.getUsername());
    }

    @Test
    void getByUsername_userNotFound_throwException() {
        when(userRepository.findByUsername("non-existing-user")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.getByUsername("non-existing-user"));
    }

    @Test
    void create_createNewUserAndGitlabUser() {
        when(userMapper.newDtoToModel(newUserDto)).thenReturn(user);

        userService.create(newUserDto);

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(gitlabService, times(1)).createUser(newUserDto);
    }

    @Test
    void getById_returnUserWithId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertEquals(userDto, result);
    }

    @Test
    void getById_userNotFound_throwException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getAllUsers_returnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void archiveUser_archiveUserAndBlockInGitlab() {
        when(userRepository.findByUsername("test-user")).thenReturn(user);

        userService.archiveUser("test-user");

        assertEquals(Role.ARCHIVED, user.getRole());
        verify(solutionService, times(1)).archiveSolutions(1L);
        verify(gitlabService, times(1)).blockUser("test-user");
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void archiveUser_userNotFound_throwException() {
        when(userRepository.findByUsername("non-existing-user")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.archiveUser("non-existing-user"));
    }

}