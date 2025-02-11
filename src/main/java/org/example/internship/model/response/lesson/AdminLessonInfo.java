package org.example.internship.model.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.model.response.task.ShortTaskInfo;

import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о занятии.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminLessonInfo {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublished;
    private List<ShortTaskInfo> tasks;
    private Long internshipId;
}
