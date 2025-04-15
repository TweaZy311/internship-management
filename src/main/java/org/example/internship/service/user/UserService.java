package org.example.internship.service.user;

import org.example.internship.entity.UserEntity;
import org.example.internship.entity.UserRole;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.request.GetUsersRequest;
import org.example.internship.model.response.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {

    /**
     * Получение информации о пользователе по его e-mail.
     *
     * @param email адрес электронной почты пользователя
     * @return информация о пользователе
     */
    UserEntity getByEmail(String email);

    /**
     * Получение информации о пользователе по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return информация о пользователе
     */
    User getById(Long id);

    /**
     * Получение информации о пользователе по его username.
     *
     * @param username имя пользователя
     * @return информация о пользователе
     */
    UserEntity getByUsername(String username);

    List<UserEntity> getUsersByInternshipIdAndRole(Long internshipId, UserRole role);

    /**
     * Получение списка всех пользователей.
     *
     * @return список пользователей
     */
    Page<UserEntity> getUsers(GetUsersRequest request);

    /**
     * Создание нового пользователя.
     *
     * @param user информация о новом пользователе
     */
    UserEntity createUser(CreateUserRequest user);

    /**
     * Архивирование данных пользователя.
     * @param username имя пользователя
     */
    UserEntity archiveUser(String username);
}
