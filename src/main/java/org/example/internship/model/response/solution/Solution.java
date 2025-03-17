package org.example.internship.model.response.solution;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.internship.model.Status;
import org.example.internship.model.response.User;
import org.example.internship.model.response.task.Task;

import java.util.Date;

/**
 * DTO для получения информации о решении задачи.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Solution {
    private Long id;
    private String repositoryUrl;
    private Date lastCommitTime;
    private String lastCommitUrl;
    private Task task;
    private User user;
    private Status status;
}
