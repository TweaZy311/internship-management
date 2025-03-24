package org.example.internship.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Сущность, представляющая решение задания.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "solution")
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
     * Список всех коммитов.
     */
    @Column(name = "commits", columnDefinition = "jsonb")
    @Type(type = "json")
    private List<Commit> commits = new ArrayList<>();

    /**
     * Комментарий к решению.
     */
    @Column(name = "comment")
    private String comment;

    /**
     * Дата и время проверки решения.
     */
    @Column(name = "checked_time")
    private Date checkedTime;

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