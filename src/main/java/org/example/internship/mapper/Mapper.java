package org.example.internship.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.example.internship.entity.*;
import org.example.internship.model.CreateUpdateStatusRequest;
import org.example.internship.model.Status;
import org.example.internship.model.request.CreateUserRequest;
import org.example.internship.model.request.application.CreateApplicationRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.model.request.lesson.CreateLessonRequest;
import org.example.internship.model.request.task.CreateTaskRequest;
import org.example.internship.model.request.task.UpdateTaskRequest;
import org.example.internship.model.response.Message;
import org.example.internship.model.response.User;
import org.example.internship.model.response.application.Application;
import org.example.internship.model.response.internship.Internship;
import org.example.internship.model.response.internship.PrivateInternshipInfo;
import org.example.internship.model.response.internship.PublicInternshipInfo;
import org.example.internship.model.response.lesson.AdminLessonInfo;
import org.example.internship.model.response.lesson.Lesson;
import org.example.internship.model.response.lesson.UserLessonInfo;
import org.example.internship.model.response.solution.Solution;
import org.example.internship.model.response.task.ShortTaskInfo;
import org.example.internship.model.response.task.Task;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Mapper extends ConfigurableMapper {
    private final ObjectMapper objectMapper;

    @Override
    protected void configure(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        factory.getConverterFactory().registerConverter(new LocalDateConverter());

        factory.classMap(CreateApplicationRequest.class, ApplicationEntity.class)
                .byDefault()
                .register();

        factory.classMap(ApplicationEntity.class, Application.class)
                .field("status", "applicationStatus")
                .byDefault()
                .register();

        factory.classMap(CreateUpdateInternshipRequest.class, InternshipEntity.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(InternshipEntity.class, Internship.class)
                .byDefault()
                .register();

        factory.classMap(InternshipEntity.class, PrivateInternshipInfo.class)
                .byDefault()
                .register();

        factory.classMap(InternshipEntity.class, PublicInternshipInfo.class)
                .byDefault()
                .register();

        factory.classMap(CreateLessonRequest.class, LessonEntity.class)
                .byDefault()
                .register();

        factory.classMap(LessonEntity.class, AdminLessonInfo.class)
                .byDefault()
                .register();

        factory.classMap(LessonEntity.class, Lesson.class)
                .byDefault()
                .register();

        factory.classMap(LessonEntity.class, UserLessonInfo.class)
                .byDefault()
                .register();

        factory.classMap(PushSystemHookEvent.class, SolutionEntity.class)
                .customize(new PushEventSolutionCustomMapper())
                .register();

        factory.classMap(SolutionEntity.class, Solution.class)
                .byDefault()
                .register();

        factory.classMap(StatusEntity.class, Status.class)
                .byDefault()
                .register();

        factory.classMap(CreateTaskRequest.class, TaskEntity.class)
                .byDefault()
                .register();

        factory.classMap(TaskEntity.class, Task.class)
                .byDefault()
                .register();

        factory.classMap(TaskEntity.class, ShortTaskInfo.class)
                .byDefault()
                .register();

        factory.classMap(UpdateTaskRequest.class, TaskEntity.class)
                .byDefault()
                .register();

        factory.classMap(CreateUserRequest.class, UserEntity.class)
                .byDefault()
                .register();

        factory.classMap(UserEntity.class, User.class)
                .byDefault()
                .register();

        factory.classMap(Message.class, MessageEntity.class)
                .byDefault()
                .register();

        factory.classMap(CreateUpdateStatusRequest.class, StatusEntity.class)
                .mapNulls(false)
                .field("name", "name")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(CreateUpdateStatusRequest request, StatusEntity statusEntity, MappingContext context) {
                        StatusType statusType = StatusType.valueOf(request.getType().toUpperCase());
                        statusEntity.setType(statusType);
                    }
                })
                .register();

        factory.classMap(Status.class, StatusEntity.class)
                .byDefault()
                .register();
    }
}
