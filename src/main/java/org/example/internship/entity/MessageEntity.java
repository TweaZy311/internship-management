package org.example.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.entity.user.UserEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность, представляющая сообщение.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "message")
public class MessageEntity {

    /**
     * ID сообщения.
     */
    @Id
    @SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    private Long id;

    /**
     * Текст сообщения.
     */
    @Column(name = "text", nullable = false)
    private String text;

    /**
     * Дата и время отправки сообщения.
     */
    @Column(name = "sent_at", nullable = false)
    @CreationTimestamp
    private Date sentAt;

    /**
     * Отправитель сообщения.
     */
    @ManyToOne
    private UserEntity sender;

    /**
     * Получатель сообщения.
     */
    @ManyToOne
    private UserEntity receiver;
}