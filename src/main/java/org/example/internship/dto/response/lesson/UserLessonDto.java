package org.example.internship.dto.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.dto.response.task.LessonTaskDto;

import java.util.List;

/**
 * DTO, предназначенная пользователю, для получения информации о занятии.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserLessonDto {
    private Long id;
    private String name;
    private String description;
    private List<LessonTaskDto> tasks;
    private Long internshipId;
}
