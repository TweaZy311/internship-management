package org.example.internship.model.request.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания нового занятия.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CreateLessonRequest {
    private String name;
    private String description;
    private Boolean isPublished = Boolean.FALSE;
    private Long internshipId;
}
