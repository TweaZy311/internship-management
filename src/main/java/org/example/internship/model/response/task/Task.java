package org.example.internship.model.response.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * DTO для получения информации о задании.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Task {
    private Long id;
    private String name;
    private String description;
    private Long lessonId;
}
