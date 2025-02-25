package org.example.internship.model.request.application;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.Status;

import java.time.LocalDate;

/**
 * DTO для создания новой заявки.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateApplicationRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String telegramId;
    private String about;
    private LocalDate birthDate;
    private String city;
    //todo status
    private Long educationStatusId;
    private String university;
    private String faculty;
    //todo rename to specialization
    private String specialty;
    //todo rename to year of study
    private Integer course;
    private Long internshipId;
}
