package org.example.internship.controller;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.internship.entity.SolutionEntity;
import org.example.internship.entity.StatusEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Page;
import org.example.internship.model.Status;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.response.solution.Solution;
import org.example.internship.service.status.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {
    private final StatusService statusService;
    private final Mapper mapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> getStatusById(@PathVariable Long id) {
        Status status = statusService.getStatusById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Status>> getStatuses(@RequestBody BaseGetListRequest request) {
        org.springframework.data.domain.Page<StatusEntity> page = statusService.getStatuses(request);

        Page<Status> result = Page.<Status>builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(mapper.mapAsList(page.getContent(), Status.class))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> createStatus(@RequestBody CreateUpdateStatusRequest request) {
        Status status = statusService.createStatus(request);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id,
                               @RequestBody CreateUpdateStatusRequest request) {
        Status status = statusService.updateStatus(id, request);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
