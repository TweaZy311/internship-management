package org.example.internship.repository;

import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по username.
     *
     * @param username имя пользователя
     * @return пользователь с указанным username
     */
    User findByUsername(String username);

    /**
     * Поиск пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты
     * @return пользователь с указанным адресом электронной почты
     */
    User findByEmail(String email);

    /**
     * Поиск всех пользователей с указанной ролью для указанной стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @param role роль пользователя
     * @return список пользователей, участвующих в указанной стажировке
     */
    List<User> findAllByInternshipIdAndRole(Long internshipId, Role role);
}
