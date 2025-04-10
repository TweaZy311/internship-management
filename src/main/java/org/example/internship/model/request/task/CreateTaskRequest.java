package org.example.internship.model.request.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания нового задания.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CreateTaskRequest {
    private String name;
    private String description;
    private Long lessonId;
}
