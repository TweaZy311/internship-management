package org.example.internship.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Сущность, представляющая стажировку.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    /**
     * Дата начала регистрации  на стажировку.
     */
    @Column(name = "registration_start_date", nullable = false)
    private LocalDate registrationStartDate;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;

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
