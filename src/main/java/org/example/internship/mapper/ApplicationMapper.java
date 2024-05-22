package org.example.internship.mapper;

import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;
import org.example.internship.model.application.Application;
import org.example.internship.model.internship.Internship;
import org.example.internship.repository.InternshipRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

/**
 * Маппер для сущности Application.
 */
@Mapper(componentModel = "spring")
public abstract class ApplicationMapper {
    private InternshipRepository internshipRepository;

    @Autowired
    public void setInternshipRepository(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    /**
     * Преобразование DTO для создания новой заявки (NewApplicationDto) в сущность Application.
     *
     * @param dto DTO для создания новой заявки
     * @return сущность Application
     */
    @Mapping(target = "status", expression = "java(org.example.internship.model.application.ApplicationStatus.SENT)")
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "educationStatus", expression = "java(org.example.internship.model.application.EducationStatus.valueOf(dto.getEducationStatus().toUpperCase()))")
    @Mapping(target = "internship", source = "internshipId", qualifiedByName = "getInternshipById")
    public abstract Application toModel(NewApplicationDto dto);

    /**
     * Преобразование сущности Application в DTO для ответа (ApplicationDto).
     *
     * @param application сущность Application
     * @return DTO для ответа
     */
    @Mapping(target = "internshipId", source = "internship", qualifiedByName = "getInternshipId")
    public abstract ApplicationDto toDto(Application application);

    /**
     * Получение сущности Internship по ID.
     *
     * @param id ID стажировки
     * @return сущность стажировки
     * @throws EntityNotFoundException если стажировка с указанным ID не найдена
     */
    @Named(value = "getInternshipById")
    public Internship getInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found with ID: " + id));
    }

    /**
     * Получение ID стажировки.
     *
     * @param internship сущность стажировки
     * @return ID стажировки
     */
    @Named(value = "getInternshipId")
    public Long getInternshipId(Internship internship) {
        return internship.getId();
    }
}
