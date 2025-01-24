package org.example.internship.service.internship;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.internship.InternshipStatusDto;
import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.ReportDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.InternshipMapper;
import org.example.internship.model.task.Task;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.internship.InternshipStatus;
import org.example.internship.model.task.Solution;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы со стажировками.
 */
@Service
@RequiredArgsConstructor
//TODO ЗАМЕНИТЬ exception и javadoc
public class InternshipServiceImpl implements InternshipService {
    private final String INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Internship with such ID could not be found";

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SolutionRepository solutionRepository;
    private final InternshipMapper internshipMapper;

    /**
     * {@inheritDoc}
     *
     * @param newInternshipDto информация о новой стажировке
     */
    @Override
    public void save(NewInternshipDto newInternshipDto) {
        internshipRepository.saveAndFlush(internshipMapper.newDtoToToModel(newInternshipDto));
    }

    /**
     * {@inheritDoc}
     *
     * @param statusDto информация о статусе стажировки
     */
    @Override
    public void changeStatus(InternshipStatusDto statusDto) {
        Internship internship = internshipRepository.findById(statusDto.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        InternshipStatus status = InternshipStatus.valueOf(statusDto.getStatus().toUpperCase());
        internship.setStatus(status);
        internshipRepository.saveAndFlush(internship);
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipDto обновленная информация о стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public void update(UpdateInternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        internshipMapper.updateDtoToModel(internship, internshipDto);
        internshipRepository.saveAndFlush(internship);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор стажировки
     * @return информация о стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public PublicInternshipDto getById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() ->  new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        if (internship.getStatus() != InternshipStatus.OPEN) {
            throw new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), "Internship is not opened");
        }
        return internshipMapper.modelToPublicDto(internship);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех стажировок
     */
    @Override
    public List<AdminInternshipDto> getAll() {
        List<Internship> internships = internshipRepository.findAll();
        return internships.stream()
                .map(internshipMapper::modelToAdminDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @return список открытых стажировок
     */
    @Override
    public List<PublicInternshipDto> getOpened() {
        List<Internship> internships = internshipRepository.findByStatus(InternshipStatus.OPEN);
        return internships.stream()
                .map(internshipMapper::modelToPublicDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус стажировки
     * @return список стажировок с указанным статусом
     */
    @Override
    public List<AdminInternshipDto> getByStatus(String status) {
        InternshipStatus internshipStatus = InternshipStatus.valueOf(status.toUpperCase());
        List<Internship> internships = internshipRepository.findByStatus(internshipStatus);
        return internships.stream()
                .map(internshipMapper::modelToAdminDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public List<ReportDto> createReport(Long internshipId) {
        internshipRepository.findById(internshipId)
                .orElseThrow(() ->  new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        List<User> users = userRepository.findAllByInternshipIdAndRole(internshipId, Role.USER);
        List<Task> tasks = taskRepository.findAllByLesson_InternshipId(internshipId);
        //todo возможно здесь не нужны exception
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No users found for internship with ID: " + internshipId);
        }
        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("No tasks found for internship with ID:" + internshipId);
        }

        List<ReportDto> reportDtos = new ArrayList<>();
        for (User user : users) {
            Map<String, String> taskStatuses = getSolutionByUserAndTasks(user, tasks);
            reportDtos.add(new ReportDto(user.getUsername(), taskStatuses));
        }
        return reportDtos;
    }

    /**
     * Получение статусов задач для пользователя.
     *
     * @param user  пользователь
     * @param tasks задачи
     * @return статус задачи
     */
    private Map<String, String> getSolutionByUserAndTasks(User user, List<Task> tasks) {
        List<Solution> solutions = solutionRepository.findAllByUserAndTaskIn(user, tasks);
        return tasks.stream()
                .collect(Collectors.toMap(
                        Task::getName,
                        task -> getTaskStatus(solutions, task).toString()
                ));
    }

    /**
     * Получение статуса задачи для пользователя.
     *
     * @param solutions решения
     * @param task      задача
     * @return статус задачи
     */
    private SolutionStatus getTaskStatus(List<Solution> solutions, Task task) {
        return solutions.stream()
                .filter(solution -> solution.getTask().equals(task))
                .findFirst()
                .map(Solution::getStatus)
                .orElse(SolutionStatus.NO_SOLUTION);
    }
}
