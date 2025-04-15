package org.example.internship.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntityType;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CreateAuditRequest {
    private String username;
    private AuditEntityType entityType;
    private AuditActionType actionType;
    private Long entityId;
    private String entityName;
    private String error;
}