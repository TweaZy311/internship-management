package org.example.internship.model.request.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для создания новой стажировки.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CreateUpdateInternshipRequest {
    private String name;
    private String description;
    private Long statusId;
    private LocalDate registrationStartDate = LocalDate.now();
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isOpen = Boolean.TRUE;
}
