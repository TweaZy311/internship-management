package org.example.internship.service.solution;

import lombok.RequiredArgsConstructor;
import org.example.internship.config.properties.StatusProperties;
import org.example.internship.entity.*;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.model.request.solution.UpdateSolutionStatusRequest;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.StatusRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.utils.SpecificationsBuilder;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса для работы с решениями заданий.
 */
@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {
    private final String SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "SolutionEntity with such ID could not be found";
    private final StatusProperties statusProperties;

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param pushEvent событие системного хука GitLab
     */
    @Override
    public SolutionEntity addSolution(PushSystemHookEvent pushEvent) {
        SolutionEntity SolutionEntity = mapper.map(pushEvent, SolutionEntity.class);
        //todo fix status
        StatusEntity statusEntity = statusRepository.findById(statusProperties.getDefaultSolutionStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such id could not be found"));

        SolutionEntity existingSolution = solutionRepository.findByRepositoryUrl(SolutionEntity.getRepositoryUrl());
        if (existingSolution != null) {
            existingSolution.setCommits(mapper.mapAsList(pushEvent.getCommits(), Commit.class));
            existingSolution.setStatus(statusEntity);
            existingSolution = solutionRepository.save(existingSolution);

            return mapper.map(existingSolution, SolutionEntity.class);
        }

        //или оставить pushEvent.getUserUsername()?
        UserEntity user = userRepository.findByUsername(pushEvent.getProject().getNamespace())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), "User with such username could not be found"));
        TaskEntity task = taskRepository.findByName(pushEvent.getProject().getName());
        SolutionEntity.setUser(user);
        SolutionEntity.setTask(task);
        SolutionEntity.setIsArchived(Boolean.FALSE);
        SolutionEntity.setStatus(statusEntity);
        SolutionEntity = solutionRepository.save(SolutionEntity);
        return mapper.map(SolutionEntity, SolutionEntity.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param updateSolutionStatusRequest информация о решении и его новом статусе
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public SolutionEntity updateSolutionStatus(UpdateSolutionStatusRequest updateSolutionStatusRequest) {
        SolutionEntity SolutionEntity = solutionRepository.findById(updateSolutionStatusRequest.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        StatusEntity status = statusRepository.findById(updateSolutionStatusRequest.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such id could not be found"));
        SolutionEntity.setStatus(status);
        SolutionEntity.setCheckedTime(new Date());
        return solutionRepository.save(SolutionEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор решения
     * @return информация о решении
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public SolutionEntity getSolutionById(Long id) {
        return solutionRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        //todo реализовать методы в маппере
//        return mapper.map(SolutionEntity, SolutionEntity.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех решений
     */
    @Override
    public Page<SolutionEntity> getSolutions(BaseGetListRequest request) {
        SpecificationsBuilder<SolutionEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return solutionRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    @Override
    public List<SolutionEntity> getAllByStatus(String status) {
        //todo FIX
        return solutionRepository.findAllByStatusIdAndIsArchived(null, true);
    }

    /**
     * {@inheritDoc}
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    @Override
    public List<SolutionEntity> getAllByTaskId(Long taskId) {
        List<SolutionEntity> solutions = solutionRepository.findAllByTaskIdAndIsArchivedFalse(taskId);
        return mapper.mapAsList(solutions, SolutionEntity.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public void archiveSolutions(Long userId) {
        List<SolutionEntity> solutions = solutionRepository.findAllByUserId(userId);
        solutions.forEach(SolutionEntity -> SolutionEntity.setIsArchived(true));
        solutionRepository.saveAll(solutions);
    }
}
