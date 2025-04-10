package org.example.internship.repository;

import org.example.internship.entity.UserRole;
import org.example.internship.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity>, JpaRepository<UserEntity, Long> {

    /**
     * Поиск пользователя по username.
     *
     * @param username имя пользователя
     * @return пользователь с указанным username
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Поиск пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты
     * @return пользователь с указанным адресом электронной почты
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Поиск всех пользователей с указанной ролью для указанной стажировки.
     *
     * @param internshipId идентификатор стажировки
     * @param role роль пользователя
     * @return список пользователей, участвующих в указанной стажировке
     */
    List<UserEntity> findAllByInternshipIdAndRole(Long internshipId, UserRole role);
}
