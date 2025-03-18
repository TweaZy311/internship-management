package org.example.internship.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

/**
 * DTO для получения информации о сообщении.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Message {
    private String text;
    private User receiver;
    private User sender;
    private Date sentAt;
}
