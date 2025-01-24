package org.example.internship.service.solution;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.solution.SolutionStatusDto;
import org.example.internship.dto.response.solution.SolutionDto;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.SolutionMapper;
import org.example.internship.model.task.Solution;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.User;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с решениями заданий.
 */
@Service
@RequiredArgsConstructor
//TODO ЗАМЕНИТЬ exception и javadoc
public class SolutionServiceImpl implements SolutionService {
    private final String SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Solution with such ID could not be found";

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SolutionMapper solutionMapper;

    /**
     * {@inheritDoc}
     *
     * @param pushEvent событие системного хука GitLab
     */
    @Override
    public void add(PushSystemHookEvent pushEvent) {
        Solution solution = solutionMapper.pushEventToModel(pushEvent);

        Solution existingSolution = solutionRepository.findByRepositoryUrl(solution.getRepositoryUrl());
        if (existingSolution != null) {
            existingSolution.setLastCommitTime(solution.getLastCommitTime());
            existingSolution.setLastCommitUrl(solution.getLastCommitUrl());
            existingSolution.setStatus(SolutionStatus.SENT);
            solutionRepository.saveAndFlush(existingSolution);
        } else {
            User user = userRepository.findByUsername(pushEvent.getUserUsername());
            Task task = taskRepository.findByName(pushEvent.getProject().getName());
            solution.setUser(user);
            solution.setTask(task);
            solutionRepository.saveAndFlush(solution);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param solutionStatusDto информация о решении и его новом статусе
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public void updateStatus(SolutionStatusDto solutionStatusDto) {
        Solution solution = solutionRepository.findById(solutionStatusDto.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        String status = solutionStatusDto.getStatus().toUpperCase();
        solution.setStatus(SolutionStatus.valueOf(status));
        solution.setCheckedTime(LocalDateTime.now());
        solutionRepository.save(solution);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор решения
     * @return информация о решении
     * @throws EntityNotFoundException если решение не найдено
     */
    @Override
    public SolutionDto getById(Long id) {
        Solution solution = solutionRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.SLN_404.getCode(), SOLUTION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return solutionMapper.modelToDto(solution);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех решений
     */
    @Override
    public List<SolutionDto> getAll() {
        List<Solution> solutions = solutionRepository.findAll();
        return solutions.stream()
                .map(solutionMapper::modelToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус решений
     * @return список решений с указанным статусом
     */
    @Override
    public List<SolutionDto> getAllByStatus(String status) {
        SolutionStatus solutionStatus = SolutionStatus.valueOf(status.toUpperCase());
        List<Solution> solutions = solutionRepository.findAllByStatusAndIsArchivedFalse(solutionStatus);
        return solutions.stream()
                .map(solutionMapper::modelToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param taskId идентификатор задания
     * @return список объектов SolutionDto, представляющих решения задания
     */
    @Override
    public List<SolutionDto> getAllByTaskId(Long taskId) {
        List<Solution> solutions = solutionRepository.findAllByTaskIdAndIsArchivedFalse(taskId);
        return solutions.stream()
                .map(solutionMapper::modelToDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public void archiveSolutions(Long userId) {
        List<Solution> solutions = solutionRepository.findAllByUserId(userId);
        solutions.forEach(solution -> solution.setIsArchived(true));
        solutionRepository.saveAllAndFlush(solutions);
    }
}
