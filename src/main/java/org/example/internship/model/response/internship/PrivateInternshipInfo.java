package org.example.internship.model.response.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.model.Status;
import org.example.internship.model.response.lesson.InternshipLessonInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о стажировке.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PrivateInternshipInfo {
    private Long id;
    private String name;
    private String description;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Boolean isOpen;
    private List<InternshipLessonInfo> lessons;
}
