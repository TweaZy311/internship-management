package org.example.internship.mapper;

import org.example.internship.dto.response.solution.SolutionDto;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.task.Task;
import org.example.internship.model.task.Solution;
import org.example.internship.model.user.User;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.gitlab4j.api.webhook.EventCommit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Маппер для сущности Solution.
 */
@Mapper(componentModel = "spring")
public abstract class SolutionMapper {
    private SolutionRepository solutionRepository;
    private UserRepository userRepository;
    private TaskRepository taskRepository;

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Преобразование push-события из GitLab (PushSystemHookEvent) в сущность Solution.
     *
     * @param pushEvent событие push из GitLab
     * @return сущность Solution
     */
    public Solution pushEventToModel(PushSystemHookEvent pushEvent) {
        if (pushEvent == null) {
            return null;
        }

        List<EventCommit> commits = pushEvent.getCommits();
        int lastCommitIndex = pushEvent.getTotalCommitsCount() - 1;

        Date commitTime = commits.get(lastCommitIndex).getTimestamp();
        LocalDateTime formattedCommitTime = commitTime.toInstant()
                .atZone(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime();

        return Solution.builder()
                .lastCommitUrl(pushEvent.getCommits().get(lastCommitIndex).getUrl())
                .lastCommitTime(formattedCommitTime)
                .repositoryUrl(pushEvent.getProject().getWebUrl())
                .status(SolutionStatus.SENT)
                .isArchived(false)
                .build();
//        Solution existingSolution = solutionRepository.findByRepositoryUrl(pushEvent.getProject().getWebUrl());
//
//        if (existingSolution != null) {
//            existingSolution.setLastCommitTime(formattedCommitTime);
//            existingSolution.setLastCommitUrl(commits.get(lastCommitIndex).getUrl());
//            existingSolution.setStatus(SolutionStatus.SENT);
//            return existingSolution;
//        } else {
//            User user = userRepository.findByUsername(pushEvent.getUserUsername());
//            Task task = taskRepository.findByName(pushEvent.getProject().getName());
//            return Solution.builder()
//                    .lastCommitUrl(pushEvent.getCommits().get(lastCommitIndex).getUrl())
//                    .lastCommitTime(formattedCommitTime)
//                    .repositoryUrl(pushEvent.getProject().getWebUrl())
//                    .user(user)
//                    .task(task)
//                    .status(SolutionStatus.SENT)
//                    .isArchived(false)
//                    .build();
//        }
    }

    /**
     * Преобразование сущности Solution в DTO для ответа (SolutionDto).
     *
     * @param solution сущность Solution
     * @return DTO для ответа
     */
    @Mapping(target = "taskId", source = "task", qualifiedByName = "getTaskId")
    @Mapping(target = "userId", source = "user", qualifiedByName = "getUserId")
    public abstract SolutionDto modelToDto(Solution solution);

    /**
     * Получение ID задания.
     *
     * @param task сущность Task
     * @return ID задания
     */
    @Named(value = "getTaskId")
    public Long getTaskId(Task task) {
        return task.getId();
    }

    /**
     * Получение ID пользователя.
     *
     * @param user сущность User
     * @return ID пользователя
     */
    @Named(value = "getUserId")
    public Long getUserId(User user) {
        return user.getId();
    }

}
