package org.example.internship.mapper;

import org.example.internship.dto.request.lesson.NewLessonDto;
import org.example.internship.dto.response.lesson.AdminLessonDto;
import org.example.internship.dto.response.lesson.InternshipLessonDto;
import org.example.internship.dto.response.lesson.UserLessonDto;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.Lesson;
import org.example.internship.repository.InternshipRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

/**
 * Маппер для сущности Lesson.
 */
@Mapper(componentModel = "spring")
public abstract class LessonMapper {
    private InternshipRepository internshipRepository;

    @Autowired
    public void setInternshipRepository(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    /**
     * Преобразование сущности Lesson в DTO для администратора (AdminLessonDto).
     *
     * @param lesson сущность Lesson
     * @return DTO для администратора
     */
    @Mapping(target = "internshipId", source = "internship", qualifiedByName = "getInternshipId")
    public abstract AdminLessonDto modelToAdminDto(Lesson lesson);

    /**
     * Преобразование DTO для создания нового занятия (NewLessonDto) в сущность Lesson.
     *
     * @param newLessonDto DTO для создания нового занятия
     * @return сущность Lesson
     */
    @Mapping(target = "isPublished", constant = "false")
    @Mapping(target = "internship", source = "internshipId", qualifiedByName = "getInternshipById")
    public abstract Lesson newLessonDtoToModel(NewLessonDto newLessonDto);

    /**
     * Преобразование сущности Lesson в DTO для пользователя (UserLessonDto).
     *
     * @param lesson сущность Lesson
     * @return DTO для пользователя
     */
    @Mapping(target = "internshipId", source = "internship", qualifiedByName = "getInternshipId")
    public abstract UserLessonDto modelToUserDto(Lesson lesson);

    /**
     * Преобразование сущности Lesson в DTO для получения краткой информации о занятии
     * в рамках стажировки (InternshipLessonDto).
     *
     * @param lesson сущность Lesson
     * @return DTO для информации о занятии в рамках стажировки
     */
    public abstract InternshipLessonDto modelToInternshipDto(Lesson lesson);

    /**
     * Получение сущности Internship по ID.
     *
     * @param id ID стажировки
     * @return сущность Internship
     * @throws EntityNotFoundException если стажировка с указанным ID не найдена
     */
    @Named(value = "getInternshipById")
    public Internship getInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found with ID: " + id));
    }

    /**
     * Получает ID стажировки.
     *
     * @param internship сущность Internship
     * @return ID стажировки
     */
    @Named(value = "getInternshipId")
    public Long getInternshipId(Internship internship) {
        return internship.getId();
    }
}
