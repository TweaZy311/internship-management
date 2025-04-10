package org.example.internship.model.request.application;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления статуса заявки.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class UpdateApplicationStatusRequest {
    private Long applicationId;
    private Long statusId;
}
