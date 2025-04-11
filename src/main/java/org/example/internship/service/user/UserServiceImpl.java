package org.example.internship.service.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.internship.config.properties.AdminProperties;
import org.example.internship.config.properties.GitlabProperties;
import org.example.internship.entity.InternshipEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.request.GetUsersRequest;
import org.example.internship.model.response.User;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.entity.UserRole;
import org.example.internship.entity.UserEntity;
import org.example.internship.model.response.UserInfo;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final String USER_NOT_FOUND_WITH = "User with such %s could not be found";

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final SolutionService solutionService;
    private final GitlabService gitlabService;

    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;
    private final GitlabProperties gitlabProperties;


    /**
     * {@inheritDoc}
     *
     * @param email адрес электронной почты пользователя
     * @return информация о пользователе
     * @throws ServiceException если пользователь с указанным email не найден
     */
    @Override
    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), String.format(USER_NOT_FOUND_WITH, "e-mail")));
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     * @return информация о пользователе
     * @throws EntityNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public UserEntity getByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), String.format(USER_NOT_FOUND_WITH, "username")));
    }

    /**
     * {@inheritDoc}
     *
     * @param createUserRequest информация о новом пользователе
     */
    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        UserEntity user = mapper.map(createUserRequest, UserEntity.class);
        user.setRole(UserRole.USER);
        if (createUserRequest.getInternshipId() != null) {
            InternshipEntity internship = internshipRepository.findById(createUserRequest.getInternshipId())
                    .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), "Internship with such ID could not be found"));
            user.setInternship(internship);
        }
        user.setPassword(passwordEncoder.encode(gitlabProperties.getUserPassword()));
        user = userRepository.save(user);
        return mapper.map(user, UserInfo.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор пользователя
     * @return информация о пользователе
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Override
    public User getById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), String.format(USER_NOT_FOUND_WITH, "ID")));
        return mapper.map(user, User.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список пользователей
     */
    @Override
    public Page<UserEntity> getUsers(GetUsersRequest request) {
        SpecificationsBuilder<UserEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();

        return userRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     */
    @Override
    public UserEntity archiveUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), String.format(USER_NOT_FOUND_WITH, "username")));;

        user.setRole(UserRole.ARCHIVED);
        solutionService.archiveSolutions(user.getId());
        gitlabService.blockUser(username);
        return userRepository.saveAndFlush(user);
    }

    /**
     * Создание пользователя-администратора, если его нет в базе данных.
     */
    @PostConstruct
    private void createAdmin() {
        if (userRepository.findByUsername(adminProperties.getUsername()).isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .email(adminProperties.getEmail())
                    .name(adminProperties.getName())
                    .username(adminProperties.getUsername())
                    .password(passwordEncoder.encode(adminProperties.getPassword()))
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.saveAndFlush(user);
        }
    }
}
