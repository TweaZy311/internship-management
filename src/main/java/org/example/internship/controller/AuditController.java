package org.example.internship.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internship.entity.AuditEntity;
import org.example.internship.entity.AuditEntityType;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.Page;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.CreateAuditRequest;
import org.example.internship.model.response.Audit;
import org.example.internship.service.audit.AuditService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditService auditService;
    private final Mapper mapper;

    @PostMapping("")
    @Operation(summary = "Add audit")
    public void addAudit(@RequestBody CreateAuditRequest request) {
        auditService.addRecord(
                request.getEntityType(),
                request.getActionType(),
                request.getEntityId(),
                request.getEntityName(),
                request.getUsername());
    }

    @PostMapping("/page")
    @Operation(summary = "Get audit with pagination")
    public Page<Audit> getAudit(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<AuditEntity> page = auditService.getPage(request);
        return Page.<Audit>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(mapper.mapAsList(page.getContent(), Audit.class))
                .build();
    }

    @GetMapping("/entity-type")
    @Operation(summary = "Get audit entity types")
    public List<AuditEntityType> getEntityTypes() {
        return Arrays.asList(AuditEntityType.values());
    }
}
