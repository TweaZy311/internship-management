package org.example.internship.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.example.internship.entity.task.SolutionEntity;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.gitlab4j.api.webhook.EventCommit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class PushEventSolutionCustomMapper extends CustomMapper<PushSystemHookEvent, SolutionEntity> {
    @Override
    public void mapAtoB(PushSystemHookEvent pushSystemHookEvent, SolutionEntity solutionEntity, MappingContext context) {
        int lastCommitIndex = pushSystemHookEvent.getTotalCommitsCount() - 1;

        EventCommit commit = pushSystemHookEvent.getCommits()
                .get(lastCommitIndex);
        LocalDateTime formattedCommitTime = commit.getTimestamp().toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();

        solutionEntity.setLastCommitTime(formattedCommitTime);
        solutionEntity.setLastCommitUrl(commit.getUrl());
        solutionEntity.setRepositoryUrl(pushSystemHookEvent.getProject().getWebUrl());
    }
}
