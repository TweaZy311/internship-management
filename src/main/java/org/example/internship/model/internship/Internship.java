package org.example.internship.model.internship;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.example.internship.model.Lesson;
import org.example.internship.model.user.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Сущность, представляющая стажировку.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "internships")
public class Internship {

    /**
     * ID стажировки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название стажировки.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание стажировки.
     */
    @Column(name = "description")
    private String description;

    /**
     * Дата начала стажировки.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Дата окончания стажировки.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Дата окончания регистрации на стажировку.
     */
    @Column(name = "registration_end_date", nullable = false)
    private LocalDate registrationEndDate;

    @Column(name = "registration_start_date", nullable = false)
    private LocalDate registrationStartDate;
    /**
     * Статус стажировки.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InternshipStatus status;

    /**
     * Список пользователей, привязанных к стажировке.
     */
    @OneToMany(mappedBy = "internship")
    @JsonIgnore
    private List<User> users;

    /**
     * Список занятий, привязанных к стажировке.
     */
    @OneToMany(mappedBy = "internship")
    @JsonIgnore
    private List<Lesson> lessons;

}
