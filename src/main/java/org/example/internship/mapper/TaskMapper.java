//package org.example.internship.mapper;
//
//import org.example.internship.model.request.task.CreateTaskRequest;
//import org.example.internship.model.request.task.UpdateTaskRequest;
//import org.example.internship.model.response.task.ShortTaskInfo;
//import org.example.internship.model.response.task.Task;
//import org.example.internship.entity.LessonEntity;
//import org.example.internship.entity.task.TaskEntity;
//import org.example.internship.repository.LessonRepository;
//import org.mapstruct.*;
//import org.mapstruct.Mapper;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.persistence.EntityNotFoundException;
//
///**
// * Маппер для сущности Task.
// */
//@Mapper(componentModel = "spring")
//public abstract class TaskMapper {
//    private LessonRepository lessonRepository;
//
//    @Autowired
//    public void setLessonRepository(LessonRepository lessonRepository) {
//        this.lessonRepository = lessonRepository;
//    }
//
//    /**
//     * Преобразование DTO для создания нового задания (NewTaskDto) в сущность Task.
//     *
//     * @param createTaskRequest DTO для создания нового задания
//     * @return сущность Task
//     */
//    @Mapping(target = "lesson", source = "lessonId", qualifiedByName = "getLessonById")
//    public abstract TaskEntity newDtoToModel(CreateTaskRequest createTaskRequest);
//
//    /**
//     * Преобразование сущности Task в DTO для ответа (TaskDto).
//     *
//     * @param task сущность Task
//     * @return DTO для ответа
//     */
//    @Mapping(target = "lessonId", source = "lesson", qualifiedByName = "getLessonId")
//    public abstract Task modelToDto(TaskEntity task);
//
//    /**
//     * Преобразование DTO для задания (TaskDto) в сущность Task.
//     *
//     * @param task DTO для задания
//     * @return сущность Task
//     */
//    @Mapping(target = "lesson", source = "lessonId", qualifiedByName = "getLessonById")
//    @Mapping(target = "repository", ignore = true)
//    @Mapping(target = "repositoryId", ignore = true)
//    public abstract TaskEntity dtoToModel(Task task);
//
//    /**
//     * Преобразование сущности Task в DTO для получения краткой информации о задании
//     * в рамках занятия (LessonTaskDto).
//     *
//     * @param task сущность Task
//     * @return DTO для информации о задании в рамках занятия
//     */
//    public abstract ShortTaskInfo modelToLessonDto(TaskEntity task);
//
//    @Mapping(target = "name", expression = "java(taskDto.getName() != null ? taskDto.getName() : task.getName())")
//    @Mapping(target = "description", expression = "java(taskDto.getDescription() != null ? taskDto.getDescription() : task.getDescription())")
//    public abstract void updateDtoToModel(@MappingTarget TaskEntity task, UpdateTaskRequest taskDto);
//
//    /**
//     * Получение сущности Lesson по ID.
//     *
//     * @param id ID занятия
//     * @return сущность Lesson
//     * @throws EntityNotFoundException если занятие с указанным ID не найдено
//     */
//    @Named("getLessonById")
//    public LessonEntity getLessonById(Long id) {
//        return lessonRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + id));
//    }
//
//    /**
//     * Получение ID занятия.
//     *
//     * @param lesson сущность Lesson
//     * @return ID занятия
//     */
//    @Named("getLessonId")
//    public Long getLessonId(LessonEntity lesson) {
//        return lesson.getId();
//    }
//
//}
