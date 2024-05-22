package org.example.internship.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.task.Task;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность, представляющая занятие.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "lessons")
public class Lesson {

    /**
     * ID занятия.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Internship internship;

    /**
     * Список заданий, привязанных к занятию.
     */
    @OneToMany(mappedBy = "lesson")
    @JsonIgnore
    private List<Task> tasks;

}