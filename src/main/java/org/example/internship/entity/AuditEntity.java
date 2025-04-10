package org.example.internship.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "audit")
public class AuditEntity {
    @Id
    @SequenceGenerator(name = "audit_seq", sequenceName = "audit_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity author;

    @CreationTimestamp
    @Column(name = "date")
    private Date date;

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private AuditActionType actionType;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private AuditEntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "error")
    private String error;
}
