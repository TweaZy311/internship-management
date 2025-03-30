package org.example.internship.model.response.solution;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.example.internship.entity.Commit;
import org.example.internship.model.Status;
import org.example.internship.model.response.User;
import org.example.internship.model.response.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO для получения информации о решении задачи.
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Solution {
    private Long id;
    private String repositoryUrl;
    private List<Commit> commits = new ArrayList<>();
    private Task task;
    private User user;
    private Status status;
}
