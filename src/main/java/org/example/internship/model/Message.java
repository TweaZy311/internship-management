package org.example.internship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая сообщение.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "message")
public class Message {

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
    private LocalDateTime sentAt;

    /**
     * Отправитель сообщения.
     */
    @ManyToOne
    private User sender;

    /**
     * Получатель сообщения.
     */
    @ManyToOne
    private User receiver;
}