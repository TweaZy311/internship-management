package org.example.internship.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * DTO для получения информации о пользователе.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Long internshipId;
}
