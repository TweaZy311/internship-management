package org.example.internship.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;

import java.util.Date;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Audit {
    private User user;
    private Date date;
    private AuditEntityType entityType;
    private AuditActionType actionType;
    private Long entityId;
    private String entityName;
    private String error;
}
