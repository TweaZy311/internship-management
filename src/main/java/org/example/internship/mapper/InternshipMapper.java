package org.example.internship.mapper;

import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.model.internship.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Маппер для сущности Internship.
 *
 */
@Mapper(componentModel = "spring")
public abstract class InternshipMapper {

    /**
     * Преобразование DTO для создания новой стажировки (NewInternshipDto) в сущность Internship.
     *
     * @param newInternshipDto DTO для создания новой стажировки
     * @return сущность Internship
     */
    @Mapping(target = "status", expression = "java(org.example.internship.model.internship.InternshipStatus.OPEN)")
    @Mapping(target = "registrationStartDate", expression = "java(java.time.LocalDate.now())")
    public abstract Internship newDtoToToModel(NewInternshipDto newInternshipDto);

    /**
     * Преобразование DTO для обновления стажировки (UpdateInternshipDto) в сущность Internship.
     *
     * @param dto DTO для обновления существующей стажировки
     */
    @Mapping(target = "description", expression = "java(dto.getDescription() != null ? dto.getDescription() : internship.getDescription())")
    public abstract void updateDtoToModel(@MappingTarget Internship internship, UpdateInternshipDto dto);

    /**
     * Преобразование сущности Internship в DTO для получения информации о стажировке
     * для публичного доступа (PublicInternshipDto).
     *
     * @param internship сущность Internship
     * @return DTO для публичного доступа
     */
    public abstract PublicInternshipDto modelToPublicDto(Internship internship);

    /**
     * Преобразование сущности Internship в DTO для получения информации о стажировке
     * для администратора (AdminInternshipDto).
     *
     * @param internship сущность Internship
     * @return DTO для администратора
     */
    public abstract AdminInternshipDto modelToAdminDto(Internship internship);
}
