package org.example.internship.dto.response.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO, для получения публичной информации о стажировке.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PublicInternshipDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
}
