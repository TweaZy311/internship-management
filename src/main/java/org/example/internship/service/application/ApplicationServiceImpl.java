package org.example.internship.service.application;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationInfo;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.mapper.ApplicationMapper;
import org.example.internship.model.Status;
import org.example.internship.model.StatusType;
import org.example.internship.model.application.Application;
import org.example.internship.model.application.ApplicationStatus;
import org.example.internship.repository.ApplicationRepository;
import org.example.internship.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Реализация сервиса для взаимодействия с заявками.
 */
@Service
@RequiredArgsConstructor

public class ApplicationServiceImpl implements ApplicationService {
    private final String APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Application with such ID could not be found";


    private final ApplicationRepository applicationRepository;
    private final StatusRepository statusRepository;
    private final ApplicationMapper applicationMapper;

    /**
     * {@inheritDoc}
     *
     * @param application информация о новой заявке
     * @throws ServiceException если заявка на указанную стажировку от этого человека уже есть
     */

    @Override
    public void save(NewApplicationDto application) {
        Application existingApplication = applicationRepository.
                findByPhoneNumberAndInternshipId(application.getPhoneNumber(),
                        application.getInternshipId());

        if (existingApplication == null) {
            applicationRepository.saveAndFlush(applicationMapper.toModel(application));
            return;
        }

        LocalDate internshipRegStartDate = existingApplication.getInternship().getRegistrationStartDate();
        Status internshipStatus = existingApplication.getInternship().getStatus();

        //todo fix comparing status
        if (internshipStatus.getName().equals("OPEN") &&
                existingApplication.getCreationDate().isBefore(internshipRegStartDate)) {
            Long id = existingApplication.getId();
            existingApplication = applicationMapper.toModel(application);
            existingApplication.setId(id);
            applicationRepository.save(existingApplication);
        } else {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.APL_400.getCode(), "Application for this internship from user with such phone number already exists");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     * @throws ServiceException если заявка с указанным идентификатором не найдена
     */
    @Override
    public void changeStatus(ApplicationStatusDto statusDto) {
        Application application = applicationRepository.findById(statusDto.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        Status status = statusRepository.findByTypeAndNameContaining(StatusType.APPLICATION, statusDto.getStatus());

        application.setStatus(status);
        applicationRepository.saveAndFlush(application);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заявок
     */
    @Override
    public List<ApplicationInfo> getAll() {
        List<Application> applications = applicationRepository.findAll();
        return applications.stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     * @throws ServiceException если заявка с указанным идентификатором не найдена
     */
    @Override
    public ApplicationInfo getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return applicationMapper.toDto(application);
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус заявки
     * @return список заявок с указанным статусом
     */
    @Override
    public List<ApplicationInfo> getByStatus(String status) {
        List<Application> applications = applicationRepository
                .findAllByStatus(ApplicationStatus.valueOf(status.toUpperCase()));
        return applications.stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок, оставленных на указанную стажировку
     */
    @Override
    public List<ApplicationInfo> getAllByInternshipId(Long internshipId) {
        List<Application> applications = applicationRepository.findAllByInternshipId(internshipId);
        return applications.stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @param status       статус заявки
     * @return список заявок, оставленных на указанную стажировку с указанным статусом
     */
    @Override
    public List<ApplicationInfo> getAllByInternshipIdAndStatus(Long internshipId, String status) {
        List<Application> applications = applicationRepository
                .findAllByInternshipIdAndStatus(internshipId, ApplicationStatus.valueOf(status.toUpperCase()));
        return applications.stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }
}
