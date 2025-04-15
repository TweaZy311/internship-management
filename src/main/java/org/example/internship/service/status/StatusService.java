package org.example.internship.service.status;


import org.example.internship.entity.StatusEntity;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.request.BaseGetListRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatusService {
    StatusEntity createStatus(CreateUpdateStatusRequest request);
    StatusEntity updateStatus(Long id, CreateUpdateStatusRequest request);
    StatusEntity getStatusById(Long id);
    Page<StatusEntity> getStatuses(BaseGetListRequest request);
    List<StatusEntity> getAllByType(String type);
}
