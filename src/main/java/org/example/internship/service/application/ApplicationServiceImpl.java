package org.example.internship.service.application;

import lombok.RequiredArgsConstructor;
import org.example.internship.config.properties.StatusProperties;
import org.example.internship.entity.InternshipEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.application.UpdateApplicationStatusRequest;
import org.example.internship.model.request.application.CreateApplicationRequest;
import org.example.internship.model.response.application.Application;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.example.internship.entity.StatusEntity;
import org.example.internship.entity.ApplicationEntity;
import org.example.internship.repository.ApplicationRepository;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


/**
 * Реализация сервиса для взаимодействия с заявками.
 */
@Service
@RequiredArgsConstructor

public class ApplicationServiceImpl implements ApplicationService {
    private final String APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND = "Application with such ID could not be found";
    private final StatusProperties statusProperties;

    private final ApplicationRepository applicationRepository;
    private final StatusRepository statusRepository;
    private final InternshipRepository internshipRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param application информация о новой заявке
     * @throws ServiceException если заявка на указанную стажировку от этого человека уже есть
     */

    @Override
    public Application saveApplication(CreateApplicationRequest application) {
        ApplicationEntity existingApplication = applicationRepository.
                findByPhoneNumberAndInternshipId(application.getPhoneNumber(),
                        application.getInternshipId());

        if (existingApplication == null) {
            InternshipEntity internship = internshipRepository.findById(application.getInternshipId())
                    .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), "Internship with such ID could not be found"));
            StatusEntity educationStatus = statusRepository.findById(application.getEducationStatusId())
                    .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such ID could not be found"));
            ApplicationEntity applicationEntity = mapper.map(application, ApplicationEntity.class);
            applicationEntity.setStatus(statusRepository.findById(statusProperties.getDefaultApplicationStatusId()).get());
            applicationEntity.setEducationStatus(educationStatus);
            applicationEntity.setInternship(internship);
            applicationEntity.setCreationDate(LocalDate.now());
            applicationEntity = applicationRepository.save(applicationEntity);
            return mapper.map(applicationEntity, Application.class);
        }

        //todo что здесь вообще происходит
        if (existingApplication.getInternship().getIsOpen()) {
            Long id = existingApplication.getId();
            existingApplication = mapper.map(application, ApplicationEntity.class);
            existingApplication.setId(id);
            existingApplication.setStatus(statusRepository.findById(statusProperties.getDefaultApplicationStatusId()).get());
            existingApplication = applicationRepository.save(existingApplication);
            return mapper.map(existingApplication, Application.class);
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
    public Application changeApplicationStatus(UpdateApplicationStatusRequest statusDto) {
        ApplicationEntity application = applicationRepository.findById(statusDto.getApplicationId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        StatusEntity status = statusRepository.findById(statusDto.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such ID could not be found"));

        application.setStatus(status);
        application = applicationRepository.saveAndFlush(application);
        return mapper.map(application, Application.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заявок
     */
    @Override
    public List<Application> getAllApplications() {
        List<ApplicationEntity> applications = applicationRepository.findAll();
        return mapper.mapAsList(applications, Application.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     * @throws ServiceException если заявка с указанным идентификатором не найдена
     */
    @Override
    public Application getApplicationById(Long id) {
        ApplicationEntity application = applicationRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        return mapper.map(application, Application.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param statusId идентификатор статуса заявки
     * @return список заявок с указанным статусом
     */
    @Override
    public List<Application> getApplicationByStatus(Long statusId) {
        List<ApplicationEntity> applications = applicationRepository
                .findAllByStatusId(statusId);
        return mapper.mapAsList(applications, Application.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @return список заявок, оставленных на указанную стажировку
     */
    @Override
    public List<Application> getAllByInternship(Long internshipId) {
        List<ApplicationEntity> applications = applicationRepository.findAllByInternshipId(internshipId);
        return mapper.mapAsList(applications, Application.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param internshipId идентификатор стажировки
     * @param statusId     идентификатор статуса заявки
     * @return список заявок, оставленных на указанную стажировку с указанным статусом
     */
    @Override
    public List<Application> getAllByInternshipAndStatus(Long internshipId, Long statusId) {
        List<ApplicationEntity> applications = applicationRepository
                .findAllByInternshipIdAndStatusId(internshipId, statusId);
        return mapper.mapAsList(applications, Application.class);
    }
}
