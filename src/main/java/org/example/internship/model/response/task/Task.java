package org.example.internship.model.response.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.response.lesson.Lesson;

/**
 * DTO для получения информации о задании.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Task {
    private Long id;
    private String name;
    private String description;
    private Lesson lesson;
}
