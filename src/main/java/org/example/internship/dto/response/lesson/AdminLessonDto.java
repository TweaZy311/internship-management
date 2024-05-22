package org.example.internship.dto.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.dto.response.task.LessonTaskDto;

import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о занятии.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminLessonDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublished;
    private List<LessonTaskDto> tasks;
    private Long internshipId;
}
