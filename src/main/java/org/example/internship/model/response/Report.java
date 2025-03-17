package org.example.internship.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * DTO для получения ведомости об успеваемости участников стажировки.
 */
@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Report {
    private String username;
    private Map<String, String> taskStatuses;
}
