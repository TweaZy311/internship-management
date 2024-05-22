package org.example.internship.model.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.model.internship.Internship;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сущность, представляющая заявку на стажировку.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "applications")
public class Application {

    /**
     * ID заявки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полное имя заявителя.
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Email заявителя.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Номер телефона заявителя.
     */
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * Имя пользователя.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * ID Telegram заявителя.
     */
    @Column(name = "telegram_id", nullable = false)
    private String telegramId;

    /**
     * Информация о заявителе.
     */
    @Column(name = "about")
    private String about;

    /**
     * Дата рождения заявителя.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * Город проживания заявителя.
     */
    @Column(name = "city", nullable = false)
    private String city;

    /**
     * Статус образования заявителя.
     */
    @Column(name = "education_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EducationStatus educationStatus;

    /**
     * Университет заявителя.
     */
    @Column(name = "university")
    private String university;

    /**
     * Факультет заявителя.
     */
    @Column(name = "faculty")
    private String faculty;

    /**
     * Специальность заявителя.
     */
    @Column(name = "specialty")
    private String specialty;

    /**
     * Курс обучения заявителя.
     */
    @Column(name = "course")
    private Integer course;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    /**
     * Статус заявки.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    /**
     * Стажировка, на которую подана заявка.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Internship internship;
}