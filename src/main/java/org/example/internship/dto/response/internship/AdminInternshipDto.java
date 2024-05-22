package org.example.internship.dto.response.internship;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.dto.response.lesson.InternshipLessonDto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO, предназначенная администратору, для получения информации о стажировке.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminInternshipDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<InternshipLessonDto> lessons;
}
