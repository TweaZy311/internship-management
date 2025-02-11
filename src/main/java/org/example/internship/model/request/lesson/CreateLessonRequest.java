package org.example.internship.model.request.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * DTO для создания нового занятия.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateLessonRequest {
    private String name;
    private String description;
    private Long internshipId;
}
