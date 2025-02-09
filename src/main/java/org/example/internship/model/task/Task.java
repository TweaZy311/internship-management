package org.example.internship.model.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.model.Lesson;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сущность, представляющая задание.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "task")
public class Task {

    /**
     * ID задания.
     */
    @Id
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    private Long id;

    /**
     * Название задания.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * URL репозитория с заданием.
     */
    @Column(name = "repository", nullable = false)
    private String repository;

    /**
     * ID репозитория с заданием.
     */
    @Column(name = "repository_id", nullable = false)
    private Long repositoryId;

    /**
     * Описание задания.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Дата публикации задания.
     */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /**
     * Занятие, к которому относится задание.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Lesson lesson;
}