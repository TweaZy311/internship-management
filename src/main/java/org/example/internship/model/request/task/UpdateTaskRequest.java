package org.example.internship.model.request.task;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления информации о задании
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class UpdateTaskRequest {
    private Long id;
    private String name;
    private String description;
}
