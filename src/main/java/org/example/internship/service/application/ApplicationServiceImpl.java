package org.example.internship.service.application;

import lombok.RequiredArgsConstructor;
import org.example.internship.config.properties.StatusProperties;
import org.example.internship.entity.InternshipEntity;
import org.example.internship.mapper.Mapper;
import org.example.internship.model.request.GetApplicationsRequest;
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
import org.example.internship.utils.SpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ApplicationEntity saveApplication(CreateApplicationRequest application) {
        ApplicationEntity existingApplication = applicationRepository.
                findByPhoneNumberAndInternshipId(application.getPhoneNumber(),
                        application.getInternshipId());
        if (existingApplication != null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.APL_400.getCode(), "Application for this internship from user with such phone number already exists");
        }

        InternshipEntity internship = internshipRepository.findById(application.getInternshipId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.ITS_404.getCode(), "Internship with such ID could not be found"));
        StatusEntity educationStatus = statusRepository.findById(application.getEducationStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such ID could not be found"));
        ApplicationEntity applicationEntity = mapper.map(application, ApplicationEntity.class);
        applicationEntity.setStatus(statusRepository.findById(statusProperties.getDefaultApplicationStatusId()).get());
        applicationEntity.setEducationStatus(educationStatus);
        applicationEntity.setInternship(internship);
        applicationEntity.setCreationDate(LocalDate.now());
        return applicationRepository.save(applicationEntity);
    }

    /**
     * {@inheritDoc}
     *
     * @param statusDto объект, содержащий идентификатор заявки и новый статус
     * @throws ServiceException если заявка с указанным идентификатором не найдена
     */
    @Override
    public ApplicationEntity changeApplicationStatus(UpdateApplicationStatusRequest statusDto) {
        ApplicationEntity application = applicationRepository.findById(statusDto.getApplicationId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
        StatusEntity status = statusRepository.findById(statusDto.getStatusId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.STS_404.getCode(), "Status with such ID could not be found"));

        application.setStatus(status);
        return applicationRepository.save(application);
    }

    /**
     * {@inheritDoc}
     *
     * @return список всех заявок
     */
    @Override
    public Page<ApplicationEntity> getApplications(GetApplicationsRequest request) {
        SpecificationsBuilder<ApplicationEntity> filterBuilder = new SpecificationsBuilder<>();
        request.getFilters().forEach(filterBuilder::with);

        Sort sort = request.getSortBy() != null ?
                Sort.by(
                        Sort.Direction.fromOptionalString(request.getSortDirection()).orElse(Sort.Direction.ASC),
                        request.getSortBy()
                ) :
                Sort.unsorted();

        return applicationRepository.findAll(filterBuilder.build(),
                PageRequest.of(request.getPage(), request.getPageSize(), sort));
    }

    /**
     * {@inheritDoc}
     *
     * @param id идентификатор заявки
     * @return информация о заявке с указанным идентификатором
     * @throws ServiceException если заявка с указанным идентификатором не найдена
     */
    @Override
    public ApplicationEntity getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, ErrorCode.APL_404.getCode(), APPLICATION_WITH_SUCH_ID_COULD_NOT_BE_FOUND));
    }
}
