package org.example.internship.service.user;

import org.example.internship.dto.request.NewUserDto;
import org.example.internship.dto.response.UserDto;

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
    UserDto getByEmail(String email);

    /**
     * Получение информации о пользователе по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return информация о пользователе
     */
    UserDto getById(Long id);

    /**
     * Получение информации о пользователе по его username.
     *
     * @param username имя пользователя
     * @return информация о пользователе
     */
    UserDto getByUsername(String username);

    /**
     * Получение списка всех пользователей.
     *
     * @return список пользователей
     */
    List<UserDto> getAllUsers();

    /**
     * Создание нового пользователя.
     *
     * @param user информация о новом пользователе
     */
    void create(NewUserDto user);

    /**
     * Архивирование данных пользователя.
     * @param username имя пользователя
     */
    void archiveUser(String username);
}
