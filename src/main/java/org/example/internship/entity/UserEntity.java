package org.example.internship.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Сущность, представляющая пользователя.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "app_user")
public class UserEntity {

    /**
     * ID пользователя.
     */
    @Id
    @SequenceGenerator(name = "app_user_seq", sequenceName = "app_user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq")
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
    private InternshipEntity internship;

    /**
     * Роль пользователя.
     */
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}