package org.example.internship.service.audit;

import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntity;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.model.request.BaseGetListRequest;
import org.springframework.data.domain.Page;

public interface AuditService {
    void addRecord(AuditEntityType entityType, AuditActionType actionType, Long entityId, String entityName, String username);
    Page<AuditEntity> getPage(BaseGetListRequest request);
}
