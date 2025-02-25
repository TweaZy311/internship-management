package org.example.internship.model.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.response.task.ShortTaskInfo;

import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о занятии.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminLessonInfo {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublished;
    private List<ShortTaskInfo> tasks;
    //todo возвращать модель
    private Long internshipId;
}
