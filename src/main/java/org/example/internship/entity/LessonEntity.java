package org.example.internship.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность, представляющая занятие.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "lesson")
public class LessonEntity {

    /**
     * ID занятия.
     */
    @Id
    @SequenceGenerator(name = "lesson_seq", sequenceName = "lesson_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_seq")
    private Long id;

    /**
     * Название занятия.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание занятия.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Флаг, указывающий, опубликовано ли занятие.
     */
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    /**
     * Стажировка, к которой относится занятие.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private InternshipEntity internship;

    /**
     * Список заданий, привязанных к занятию.
     */
    @OneToMany(mappedBy = "lesson")
    @JsonIgnore
    private List<TaskEntity> tasks;

}