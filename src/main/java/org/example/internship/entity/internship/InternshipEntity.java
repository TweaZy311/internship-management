package org.example.internship.entity.internship;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.example.internship.entity.LessonEntity;
import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.user.UserEntity;

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
@Table(name = "internship")
public class InternshipEntity {

    /**
     * ID стажировки.
     */
    @Id
    @SequenceGenerator(name = "internship_seq", sequenceName = "internship_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "internship_seq")
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
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusEntity status;

    /**
     * Список пользователей, привязанных к стажировке.
     */
    @OneToMany(mappedBy = "internship")
    @JsonIgnore
    private List<UserEntity> users;

    /**
     * Список занятий, привязанных к стажировке.
     */
    @OneToMany(mappedBy = "internship")
    @JsonIgnore
    private List<LessonEntity> lessons;

}
