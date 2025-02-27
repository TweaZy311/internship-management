package org.example.internship.model.response.internship;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.Status;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Internship {
    private Long id;
    private String name;
    private String description;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Boolean isOpen;
}
