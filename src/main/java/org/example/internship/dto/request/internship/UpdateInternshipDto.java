package org.example.internship.dto.request.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateInternshipDto {
    private Long id;
    private String description;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
}
