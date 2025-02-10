package org.example.internship.service.gitlab;


import lombok.RequiredArgsConstructor;
import org.example.internship.config.properties.AdminProperties;
import org.example.internship.config.properties.GitlabProperties;
import org.example.internship.model.request.NewUserDto;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Реализация сервиса для взаимодействия с GitLab.
 */
@Service
@RequiredArgsConstructor
public class GitlabServiceImpl implements GitlabService {
    private GitLabApi gitlabApi;
    private final GitlabProperties gitlabProperties;
    private final AdminProperties adminProperties;

    private final String HOOK_URL = "http://backend:8080/api/solution/add";

    /**
     * {@inheritDoc}
     *
     * @param repositoryName название репозитория
     * @param description    описание репозитория
     * @return созданный проект в GitLab
     * @throws ServiceException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public Project createRepository(String repositoryName, String description) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        Project project;
        try {
            project = projectApi.createProject(repositoryName);
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to create project");
        }
        CommitsApi commitsApi = gitlabApi.getCommitsApi();
        CommitPayload commitPayload = new CommitPayload();
        CommitAction commitAction = new CommitAction();

        commitAction.withAction(CommitAction.Action.CREATE)
                .withFilePath("README.md")
                .withContent(description);

        commitPayload.withAction(commitAction)
                .withBranch("main")
                .withCommitMessage("initial commit")
                .withAuthorName(adminProperties.getUsername());
        try {
            commitsApi.createCommit(project.getId(), commitPayload);
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to create initial commit");
        }

        return project;
    }

    /**
     * {@inheritDoc}
     *
     * @param repositoryId    идентификатор репозитория, который необходимо форкнуть
     * @param targetNamespace пространство имен, в котором создается форк
     * @throws ServiceException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public void forkRepository(Long repositoryId, String targetNamespace) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        try {
            projectApi.forkProject(repositoryId, targetNamespace);
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to fork project");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param newUserDto информация о новом пользователе
     * @throws ServiceException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public void createUser(NewUserDto newUserDto) {
        UserApi userApi = gitlabApi.getUserApi();
        User user = new User();
        user.setUsername(newUserDto.getUsername());
        user.setEmail(newUserDto.getEmail());
        user.setName(newUserDto.getName());
        try {
            userApi.createUser(user, gitlabProperties.getUserPassword(), false);
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to create user in GitLab");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param projectId идентификатор проекта
     * @return true, если проект был форкнут, иначе false
     * @throws ServiceException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public boolean isForkedRepository(Long projectId) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        Project project;
        try {
            project = projectApi.getProject(projectId);
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to check if project exists");
        }

        return project.getForkedFromProject() != null;
    }

    /**
     * {@inheritDoc}
     *
     * @param username имя пользователя
     */
    @Override
    public void blockUser(String username) {
        UserApi userApi = gitlabApi.getUserApi();
        User user;
        try {
            user = userApi.getUser(username);
            //блокировка тк при удалении пропадают все репо
            userApi.blockUser(user.getId());
        } catch (GitLabApiException e){
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to block user");
        }
    }

    @PostConstruct
    private void initGitlabApi() {
        this.gitlabApi = new GitLabApi(gitlabProperties.getUrl(), gitlabProperties.getPersonalAccessToken());
        addSystemHook();
    }

    /**
     * Добавление системного хука для обработки событий GitLab.
     *
     * @throws ServiceException если произошла ошибка при взаимодействии с GitLab API
     */
    private void addSystemHook() {
        SystemHooksApi systemHooksApi = gitlabApi.getSystemHooksApi();
        try {
            //если хук с такими параметрами уже есть, то точно такой же не создастся
            List<SystemHook> existingHooks = systemHooksApi.getSystemHooks();
            boolean hookExists = existingHooks.stream()
                    .anyMatch(hook -> hook.getUrl().equals(HOOK_URL) && hook.getPushEvents());
            if (!hookExists) {
                SystemHook systemHook = new SystemHook().withPushEvents(true)
                        .withUrl(HOOK_URL)
                        .withRepositoryUpdateEvents(false);
                systemHooksApi.addSystemHook(HOOK_URL, gitlabProperties.getSystemHookToken(), systemHook);
            }
        } catch (GitLabApiException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, ErrorCode.GLB_500.getCode(), "Unable to add system hook");
        }
    }
}
