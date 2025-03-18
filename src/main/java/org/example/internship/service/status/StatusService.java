package org.example.internship.service.status;


import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Status;

import java.util.List;

public interface StatusService {
    Status createStatus(CreateUpdateStatusRequest request);
    Status updateStatus(Long id, CreateUpdateStatusRequest request);
    Status getStatusById(Long id);
    List<Status> getAllStatuses();
    List<Status> getAllByType(String type);
}
