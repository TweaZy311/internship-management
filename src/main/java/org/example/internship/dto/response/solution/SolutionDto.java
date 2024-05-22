package org.example.internship.dto.response.solution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.model.task.SolutionStatus;

import java.time.LocalDateTime;

/**
 * DTO для получения информации о решении задачи.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolutionDto {
    private Long id;
    private String repositoryUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCommitTime;
    private String lastCommitUrl;
    private Long taskId;
    private Long userId;
    private SolutionStatus status;
}
