package org.example.internship.service.solution;

import lombok.RequiredArgsConstructor;
import org.example.internship.config.properties.StatusProperties;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.solution.UpdateSolutionStatusRequest;
import org.example.internship.model.response.solution.Solution;
import org.example.internship.entity.StatusEntity;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.entity.SolutionEntity;
import org.example.internship.entity.TaskEntity;
import org.example.internship.entity.UserEntity;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.StatusRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
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
    private final String SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Solution with such ID could not be found";
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
    public Solution add(PushSystemHookEvent pushEvent) {
        SolutionEntity solution = mapper.map(pushEvent, SolutionEntity.class);
        //todo fix status
        StatusEntity statusEntity = statusRepository.findById(statusProperties.getDefaultSolutionStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such id could not be found"));

        SolutionEntity existingSolution = solutionRepository.findByRepositoryUrl(solution.getRepositoryUrl());
        if (existingSolution != null) {
            existingSolution.setLastCommitTime(solution.getLastCommitTime());
            existingSolution.setLastCommitUrl(solution.getLastCommitUrl());
            existingSolution.setStatus(statusEntity);
            existingSolution = solutionRepository.save(existingSolution);

            return mapper.map(existingSolution, Solution.class);
        }

        //или оставить pushEvent.getUserUsername()?
        UserEntity user = userRepository.findByUsername(pushEvent.getProject().getNamespace())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.USR_404.getCode(), "User with such username could not be found"));
        TaskEntity task = taskRepository.findByName(pushEvent.getProject().getName());
        solution.setUser(user);
        solution.setTask(task);
        solution.setIsArchived(Boolean.FALSE);
        solution.setStatus(statusEntity);
        solution = solutionRepository.save(solution);
        return mapper.map(solution, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param updateSolutionStatusRequest информация о решении и его новом статусе
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public Solution updateStatus(UpdateSolutionStatusRequest updateSolutionStatusRequest) {
        SolutionEntity solution = solutionRepository.findById(updateSolutionStatusRequest.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        StatusEntity status = statusRepository.findById(updateSolutionStatusRequest.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such id could not be found"));
        solution.setStatus(status);
        solution.setCheckedTime(new Date());
        solution = solutionRepository.save(solution);
        return mapper.map(solution, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор решения
     * @return информация о решении
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public Solution getById(Long id) {
        SolutionEntity solution = solutionRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        //todo реализовать методы в маппере
        return mapper.map(solution, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех решений
     */
    @Override
    public List<Solution> getAll() {
        List<SolutionEntity> solutions = solutionRepository.findAll();

        return mapper.mapAsList(solutions, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    @Override
    public List<Solution> getAllByStatus(String status) {
        //todo FIX
        List<SolutionEntity> solutions = solutionRepository.findAllByStatusIdAndIsArchived(null, true);
        return mapper.mapAsList(solutions, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    @Override
    public List<Solution> getAllByTaskId(Long taskId) {
        List<SolutionEntity> solutions = solutionRepository.findAllByTaskIdAndIsArchivedFalse(taskId);
        return mapper.mapAsList(solutions, Solution.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public void archiveSolutions(Long userId) {
        List<SolutionEntity> solutions = solutionRepository.findAllByUserId(userId);
        solutions.forEach(solution -> solution.setIsArchived(true));
        solutionRepository.saveAllAndFlush(solutions);
    }
}
