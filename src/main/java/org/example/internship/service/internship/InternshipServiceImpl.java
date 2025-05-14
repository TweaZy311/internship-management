package org.example.internship.service.internship;

import lombok.RequiredArgsConstructor;
import org.example.internship.entity.*;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.internship.UpdateInternshipStatusRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.model.response.Report;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.repository.*;
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class InternshipServiceImpl implements InternshipService {
    private final String INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Internship with such ID could not be found";

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SolutionRepository solutionRepository;
    private final StatusRepository statusRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param createUpdateInternshipRequest информация о новой стажировке
     */
    @Override
    public InternshipEntity saveInternship(CreateUpdateInternshipRequest createUpdateInternshipRequest) {
        InternshipEntity internshipEntity = mapper.map(createUpdateInternshipRequest, InternshipEntity.class);
        StatusEntity status = statusRepository.findById(createUpdateInternshipRequest.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such ID could not be found"));
        internshipEntity.setStatus(status);
        return internshipRepository.save(internshipEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param updateInternshipStatusRequest информация о статусе стажировки
     */
    @Override
    //todo remove
    public InternshipEntity changeInternshipStatus(UpdateInternshipStatusRequest updateInternshipStatusRequest) {
        InternshipEntity internship = internshipRepository.findById(updateInternshipStatusRequest.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        StatusEntity status = statusRepository.findById(updateInternshipStatusRequest.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such id was not found"));
        internship.setStatus(status);
        return internshipRepository.save(internship);
    }

    /**
     * {@inheritDoc}
     *
     * @param createUpdateInternshipRequest обновленная информация о стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public InternshipEntity updateInternship(Long id, CreateUpdateInternshipRequest createUpdateInternshipRequest) {
        InternshipEntity internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        mapper.map(createUpdateInternshipRequest, internship);
        return internshipRepository.save(internship);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор стажировки
     * @return информация о стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public InternshipEntity getInternshipById(Long id) {
        InternshipEntity internship = internshipRepository.findById(id)
                .orElseThrow(() ->  new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return internship;
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех стажировок
     */
    @Override
    public Page<InternshipEntity> getInternships(BaseGetListRequest request) {
        SpecificationsBuilder<InternshipEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();
        return internshipRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @return список открытых стажировок
     */
    @Override
    //todo этот метод вообще не нужен если есть тот что ниже
    public List<InternshipEntity> getInternshipsByIsOpen(Boolean isOpen) {
        return internshipRepository.findByIsOpen(isOpen);
    }

    /**
     * {@inheritDoc}
     *
     * @param statusId идентификатор статуса стажировки
     * @return список стажировок с указанным статусом
     */
    @Override
    public List<InternshipEntity> getInternshipsByStatus(Long statusId) {
        return internshipRepository.findByStatusId(statusId);
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return ведомость по стажировке
     * @throws ServiceException если стажировка не найдена
     */
    @Override
    public List<Report> createReport(Long internshipId) {
        internshipRepository.findById(internshipId)
                .orElseThrow(() ->  new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), INTERNSHIP_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        List<UserEntity> users = userRepository.findAllByInternshipIdAndRole(internshipId, UserRole.USER);
        List<TaskEntity> tasks = taskRepository.findAllByLesson_InternshipId(internshipId);
//
//        if (users.isEmpty()) {
//            throw new EntityNotFoundException("No users found for internship with ID: " + internshipId);
//        }
//        if (tasks.isEmpty()) {
//            throw new EntityNotFoundException("No tasks found for internship with ID:" + internshipId);
//        }

        List<Report> reports = new ArrayList<>();
        for (UserEntity user : users) {
            Map<String, String> taskStatuses = getSolutionByUserAndTasks(user, tasks);
            reports.add(new Report(user.getUsername(), taskStatuses));
        }
        return reports;
    }

    /**
     * Получение статусов задач для пользователя.
     *
     * @param user  пользователь
     * @param tasks задачи
     * @return статус задачи
     */
    private Map<String, String> getSolutionByUserAndTasks(UserEntity user, List<TaskEntity> tasks) {
        List<SolutionEntity> solutions = solutionRepository.findAllByUserAndTaskIn(user, tasks);
        return tasks.stream()
                .collect(Collectors.toMap(
                        TaskEntity::getName,
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
    private StatusEntity getTaskStatus(List<SolutionEntity> solutions, TaskEntity task) {
        return solutions.stream()
                .filter(solution -> solution.getTask().equals(task))
                .findFirst()
                .map(SolutionEntity::getStatus)
                .get();
    }
}
