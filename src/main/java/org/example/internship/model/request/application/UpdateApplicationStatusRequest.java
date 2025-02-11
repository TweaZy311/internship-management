package org.example.internship.model.request.application;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * DTO для обновления статуса заявки.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateApplicationStatusRequest {
    private Long id;
    private String status;
}
