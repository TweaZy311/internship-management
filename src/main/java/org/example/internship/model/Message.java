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
@Table(name = "messages")
public class Message {

    /**
     * ID сообщения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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