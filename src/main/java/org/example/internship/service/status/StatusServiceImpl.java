package org.example.internship.service.status;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.StatusType;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.repository.StatusRepository;
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public StatusEntity createStatus(CreateUpdateStatusRequest request) {
        StatusEntity statusEntity = mapper.map(request, StatusEntity.class);
        return statusRepository.save(statusEntity);
    }

    @Override
    public StatusEntity updateStatus(Long id, CreateUpdateStatusRequest request) {
        StatusEntity existingStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), STATUS_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        mapper.map(request, existingStatus);
        return statusRepository.save(existingStatus);
    }

    @Override
    public StatusEntity getStatusById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), STATUS_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
    }

    @Override
    public Page<StatusEntity> getStatuses(BaseGetListRequest request) {
        SpecificationsBuilder<StatusEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return statusRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    @Override
    public List<StatusEntity> getAllByType(String type) {
        return statusRepository.findAllByType(StatusType.valueOf(type.toUpperCase()));
    }
}
