package org.example.internship.service.user;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.NewUserDto;
import org.example.internship.dto.response.UserDto;
import org.example.internship.mapper.UserMapper;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SolutionService solutionService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final GitlabService gitlabService;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.password}")
    private String adminPassword;

    /**
     * {@inheritDoc}
     *
     * @param email адрес электронной почты пользователя
     * @return информация о пользователе
     * @throws EntityNotFoundException если пользователь с указанным email не найден
     */
    @Override
    public UserDto getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        return userMapper.modelToDto(user);
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     * @return информация о пользователе
     * @throws EntityNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        return userMapper.modelToDto(user);
    }

    /**
     * {@inheritDoc}
     *
     * @param newUserDto информация о новом пользователе
     */
    @Override
    public void create(NewUserDto newUserDto) {
        User user = userMapper.newDtoToModel(newUserDto);
        userRepository.saveAndFlush(user);
        gitlabService.createUser(newUserDto);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор пользователя
     * @return информация о пользователе
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + id));
        return userMapper.modelToDto(user);
    }

    /**
     * {@inheritDoc}
     *
     * @return список пользователей
     */
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::modelToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     */
    @Override
    public void archiveUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        user.setRole(Role.ARCHIVED);
        solutionService.archiveSolutions(user.getId());
        gitlabService.blockUser(username);
        userRepository.saveAndFlush(user);
    }

    /**
     * Создание пользователя-администратора, если его нет в базе данных.
     */
    @PostConstruct
    private void createAdmin() {
        if (userRepository.findByUsername(adminUsername) == null) {
            User user = User.builder()
                    .email(adminEmail)
                    .name(adminName)
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            userRepository.saveAndFlush(user);
        }
    }
}
