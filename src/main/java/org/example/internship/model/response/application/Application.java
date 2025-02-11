package org.example.internship.model.response.application;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.model.Status;

import java.time.LocalDate;

/**
 * DTO для получения информации о заявке.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Application {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private String telegramId;
    private String about;
    private LocalDate birthDate;
    private LocalDate creationDate;
    private String city;
    private Status educationStatus;
    private Status applicationStatus;
    private String university;
    private String faculty;
    private String specialty;
    private Integer course;
    private Long internshipId;
}
