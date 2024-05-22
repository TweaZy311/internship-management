package org.example.internship.dto.response.lesson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * DTO, содержащая краткую информацию о занятии.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InternshipLessonDto {
    private Long id;
    private String name;
    private String description;
}
