package org.example.internship.service.status;


import org.example.internship.entity.StatusEntity;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Status;
import org.example.internship.model.request.BaseGetListRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatusService {
    Status createStatus(CreateUpdateStatusRequest request);
    Status updateStatus(Long id, CreateUpdateStatusRequest request);
    Status getStatusById(Long id);
    Page<StatusEntity> getStatuses(BaseGetListRequest request);
    List<Status> getAllByType(String type);
}
