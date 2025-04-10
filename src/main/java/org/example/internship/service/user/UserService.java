package org.example.internship.service.user;

import org.example.internship.entity.UserEntity;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.request.GetUsersRequest;
import org.example.internship.model.response.User;
import org.springframework.data.domain.Page;

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
    User getByEmail(String email);

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
    User getByUsername(String username);

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
    User createUser(CreateUserRequest user);

    /**
     * Архивирование данных пользователя.
     * @param username имя пользователя
     */
    void archiveUser(String username);
}
