package org.example.internship.dto.request.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO для создания новой стажировки.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewInternshipDto {
    private String name;
    private String description;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
}
