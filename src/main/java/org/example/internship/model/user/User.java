package org.example.internship.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.model.internship.Internship;

import javax.persistence.*;

/**
 * Сущность, представляющая пользователя.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "users")
public class User {

    /**
     * ID пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Email пользователя.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Стажировка, к которой привязан пользователь.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Internship internship;

    /**
     * Роль пользователя.
     */
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
}