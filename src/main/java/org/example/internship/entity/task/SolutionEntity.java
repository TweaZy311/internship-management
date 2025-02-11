package org.example.internship.entity.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая решение задания.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "task_solution")
public class SolutionEntity {

    /**
     * ID решения.
     */
    @Id
    @SequenceGenerator(name = "solution_seq", sequenceName = "solution_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solution_seq")
    private Long id;

    /**
     * URL репозитория с решением.
     */
    @Column(name = "repository_url", nullable = false, unique = true)
    private String repositoryUrl;

    /**
     * Дата и время последнего коммита.
     */
    @Column(name = "last_commit_time", nullable = false)
    private LocalDateTime lastCommitTime;

    /**
     * URL последнего коммита.
     */
    @Column(name = "last_commit_url", nullable = false)
    private String lastCommitUrl;

    /**
     * Комментарий к решению.
     */
    @Column(name = "comment")
    private String comment;

    /**
     * Дата и время проверки решения.
     */
    @Column(name = "checked_time")
    private LocalDateTime checkedTime;

    /**
     * Флаг, указывающий находится ли решение в архиве.
     */
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    /**
     * Пользователь, предоставивший решение.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    /**
     * Задание, к которому относится решение.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private TaskEntity task;

    /**
     * Статус решения.
     */
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusEntity status;
}