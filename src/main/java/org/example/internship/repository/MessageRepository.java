package org.example.internship.repository;

import org.example.internship.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сообщениями.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * Поиск сообщений, отправленных или полученных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список сообщений, отправленных или полученных указанным пользователем
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<MessageEntity> findBySenderIdOrReceiverId(Long userId);
}
