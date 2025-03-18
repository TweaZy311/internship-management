package org.example.internship.controller;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Status;
import org.example.internship.service.status.StatusService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {
    private final StatusService statusService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Status getStatusById(@PathVariable Long id) {
        return statusService.getStatusById(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Status> getAllStatus(@RequestParam(required = false) String type) {
        if (StringUtils.isEmpty(type)) {
            return statusService.getAllStatuses();
        }
        return statusService.getAllByType(type);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Status createStatus(@RequestBody CreateUpdateStatusRequest request) {
        return statusService.createStatus(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Status createStatus(@PathVariable Long id,
                               @RequestBody CreateUpdateStatusRequest request) {
        return statusService.updateStatus(id, request);
    }
}
