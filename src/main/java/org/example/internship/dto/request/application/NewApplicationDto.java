package org.example.internship.dto.request.application;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO для создания новой заявки.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewApplicationDto {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private String telegramId;
    private String about;
    private LocalDate birthDate;
    private String city;
    private String educationStatus;
    private String university;
    private String faculty;
    private String specialty;
    private Integer course;
    private Long internshipId;
}
