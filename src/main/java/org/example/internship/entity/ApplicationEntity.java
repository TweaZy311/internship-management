package org.example.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "application")
public class ApplicationEntity {

    /**
     * ID заявки.
     */
    @Id
    @SequenceGenerator(name = "application_seq", sequenceName = "application_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
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
    @ManyToOne
    @JoinColumn(name = "education_status_id")
    private StatusEntity educationStatus;

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
    //todo rename to specialization
    private String specialty;

    /**
     * Курс обучения заявителя.
     */
    @Column(name = "course")
    //todo rename to year of study
    private Integer course;

    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDate creationDate;

    /**
     * Статус заявки.
     */
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusEntity status;

    /**
     * Стажировка, на которую подана заявка.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private InternshipEntity internship;
}