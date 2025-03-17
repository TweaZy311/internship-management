package org.example.internship.service.status;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.StatusType;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Status;
import org.example.internship.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final String STATUS_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Status with such ID could not be found";

    private final StatusRepository statusRepository;
    private final Mapper mapper;

    @Override
    public Status createStatus(CreateUpdateStatusRequest request) {
        StatusEntity statusEntity = mapper.map(request, StatusEntity.class);
        return mapper.map(statusRepository.save(statusEntity), Status.class);
    }

    @Override
    public Status updateStatus(Long id, CreateUpdateStatusRequest request) {
        StatusEntity existingStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), STATUS_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        mapper.map(request, existingStatus);
        return mapper.map(statusRepository.save(existingStatus), Status.class);
    }

    @Override
    public Status getStatusById(Long id) {
        StatusEntity statusEntity = statusRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), STATUS_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return mapper.map(statusEntity, Status.class);
    }

    @Override
    public List<Status> getAllStatuses() {
        List<StatusEntity> statusEntities = statusRepository.findAll();
        return mapper.mapAsList(statusEntities, Status.class);
    }

    @Override
    public List<Status> getAllByType(String type) {
        List<StatusEntity> statusEntities = statusRepository.findAllByType(StatusType.valueOf(type.toUpperCase()));
        return mapper.mapAsList(statusEntities, Status.class);
    }
}
