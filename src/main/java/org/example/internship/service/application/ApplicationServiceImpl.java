package org.example.internship.service.application;

import lombok.RequiredArgsConstructor;
import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;
import org.example.internship.mapper.ApplicationMapper;
import org.example.internship.model.application.Application;
import org.example.internship.model.application.ApplicationStatus;
import org.example.internship.model.internship.InternshipStatus;
import org.example.internship.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Реализация сервиса для взаимодействия с заявками.
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;

    /**
     * {@inheritDoc}
     *
     * @param application информация о новой заявке
     * @throws EntityExistsException если заявка на указанную стажировку от этого человека уже есть
     */

    @Override
    public void save(NewApplicationDto application) {
        Application existingApplication = applicationRepository.
                findByPhoneNumberAndInternshipId(application.getPhoneNumber(),
                        application.getInternshipId());

        if (existingApplication != null) {
            LocalDate internshipRegStartDate = existingApplication.getInternship().getRegistrationStartDate();
            InternshipStatus internshipStatus = existingApplication.getInternship().getStatus();

            if (internshipStatus.equals(InternshipStatus.OPEN) &&
                    existingApplication.getCreationDate().isBefore(internshipRegStartDate)) {
                Long id = existingApplication.getId();
                existingApplication = applicationMapper.toModel(application);
                existingApplication.setId(id);
                applicationRepository.save(existingApplication);
            } else {
                throw new EntityExistsException(String.format("Application to internship with id %s from person with number %s already exists",
                        application.getInternshipId(),
                        application.getPhoneNumber()));
            }
        } else {
            applicationRepository.saveAndFlush(applicationMapper.toModel(application));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     * @throws EntityNotFoundException если заявка с указанным идентификатором не найдена
     */
    @Override
    public void changeStatus(ApplicationStatusDto statusDto) {
        Application application = applicationRepository.findById(statusDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Application not found with ID:" + statusDto.getId()));
        ApplicationStatus status = ApplicationStatus.valueOf(statusDto.getStatus().toUpperCase());
        application.setStatus(status);
        applicationRepository.saveAndFlush(application);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заявок
     */
    @Override
    public List<ApplicationDto> getAll() {
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
     * @throws EntityNotFoundException если заявка с указанным идентификатором не найдена
     */
    @Override
    public ApplicationDto getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with ID:" + id));
        return applicationMapper.toDto(application);
    }

    /**
     * {@inheritDoc}
     *
     * @param status статус заявки
     * @return список заявок с указанным статусом
     */
    @Override
    public List<ApplicationDto> getByStatus(String status) {
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
    public List<ApplicationDto> getAllByInternshipId(Long internshipId) {
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
    public List<ApplicationDto> getAllByInternshipIdAndStatus(Long internshipId, String status) {
        List<Application> applications = applicationRepository
                .findAllByInternshipIdAndStatus(internshipId, ApplicationStatus.valueOf(status.toUpperCase()));
        return applications.stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }
}
