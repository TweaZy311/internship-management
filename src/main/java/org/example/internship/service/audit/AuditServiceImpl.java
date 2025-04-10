package org.example.internship.service.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internship.config.properties.AuditProperties;
import org.example.internship.entity.AuditActionType;
import org.example.internship.entity.AuditEntity;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.repository.AuditRepository;
import org.example.internship.service.user.UserService;
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditProperties auditProperties;

    private final AuditRepository auditRepository;
    private final UserService userService;

    @Override
    public void addRecord(AuditEntityType entityType, AuditActionType actionType, Long entityId, String entityName, String username) {
        if (!auditProperties.getEnabled()) {
            return;
        }
        AuditEntity auditEntity = AuditEntity.builder()
                .author(userService.getByUsername(username))
                .entityType(entityType != null ? entityType : AuditEntityType.SYSTEM)
                .actionType(actionType)
                .entityId(entityId)
                .entityName(entityName)
                .build();
        auditRepository.save(auditEntity);
    }

    @Override
    public Page<AuditEntity> getPage(BaseGetListRequest request) {
        SpecificationsBuilder<AuditEntity> specificationsBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(specificationsBuilder::with);
        Specification<AuditEntity> specification = specificationsBuilder.build();
        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return auditRepository.findAll(specification, PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }
}
