package org.example.internship.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания нового сообщения.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CreateMessageRequest {
    private String text;
    private Long senderId;
    private Long receiverId;
}
