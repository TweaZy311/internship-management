package org.example.internship.mapper;

import org.example.internship.dto.request.task.NewTaskDto;
import org.example.internship.dto.request.task.UpdateTaskDto;
import org.example.internship.dto.response.task.LessonTaskDto;
import org.example.internship.dto.response.task.TaskDto;
import org.example.internship.model.Lesson;
import org.example.internship.model.task.Task;
import org.example.internship.repository.LessonRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

/**
 * Маппер для сущности Task.
 */
@Mapper(componentModel = "spring")
public abstract class TaskMapper {
    private LessonRepository lessonRepository;

    @Autowired
    public void setLessonRepository(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Преобразование DTO для создания нового задания (NewTaskDto) в сущность Task.
     *
     * @param newTaskDto DTO для создания нового задания
     * @return сущность Task
     */
    @Mapping(target = "lesson", source = "lessonId", qualifiedByName = "getLessonById")
    public abstract Task newDtoToModel(NewTaskDto newTaskDto);

    /**
     * Преобразование сущности Task в DTO для ответа (TaskDto).
     *
     * @param task сущность Task
     * @return DTO для ответа
     */
    @Mapping(target = "lessonId", source = "lesson", qualifiedByName = "getLessonId")
    public abstract TaskDto modelToDto(Task task);

    /**
     * Преобразование DTO для задания (TaskDto) в сущность Task.
     *
     * @param taskDto DTO для задания
     * @return сущность Task
     */
    @Mapping(target = "lesson", source = "lessonId", qualifiedByName = "getLessonById")
    @Mapping(target = "repository", ignore = true)
    @Mapping(target = "repositoryId", ignore = true)
    public abstract Task dtoToModel(TaskDto taskDto);

    /**
     * Преобразование сущности Task в DTO для получения краткой информации о задании
     * в рамках занятия (LessonTaskDto).
     *
     * @param task сущность Task
     * @return DTO для информации о задании в рамках занятия
     */
    public abstract LessonTaskDto modelToLessonDto(Task task);

    @Mapping(target = "name", expression = "java(taskDto.getName() != null ? taskDto.getName() : task.getName())")
    @Mapping(target = "description", expression = "java(taskDto.getDescription() != null ? taskDto.getDescription() : task.getDescription())")
    public abstract void updateDtoToModel(@MappingTarget Task task, UpdateTaskDto taskDto);

    /**
     * Получение сущности Lesson по ID.
     *
     * @param id ID занятия
     * @return сущность Lesson
     * @throws EntityNotFoundException если занятие с указанным ID не найдено
     */
    @Named("getLessonById")
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + id));
    }

    /**
     * Получение ID занятия.
     *
     * @param lesson сущность Lesson
     * @return ID занятия
     */
    @Named("getLessonId")
    public Long getLessonId(Lesson lesson) {
        return lesson.getId();
    }

}
