package org.example.internship.service.gitlab;

import org.example.internship.dto.request.NewUserDto;
import org.example.internship.exception.GitlabException;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Реализация сервиса для взаимодействия с GitLab.
 */
@Service
public class GitlabServiceImpl implements GitlabService {
    private final GitLabApi gitlabApi;

    @Value("${gitlab.system.hook.token}")
    private String hookToken;

    @Value("${user.password}")
    private String userPassword;

    private final String HOOK_URL = "http://backend:8080/api/solution/add";

    /**
     * Конструктор для инициализации GitLab API.
     *
     * @param gitlabUrl           URL-адрес GitLab
     * @param personalAccessToken персональный токен доступа
     */
    @Autowired
    public GitlabServiceImpl(@Value("${gitlab.url}") String gitlabUrl,
                             @Value("${gitlab.access.token}") String personalAccessToken) {
        this.gitlabApi = new GitLabApi(gitlabUrl, personalAccessToken);
    }

    /**
     * {@inheritDoc}
     *
     * @param repositoryName название репозитория
     * @param description    описание репозитория
     * @return созданный проект в GitLab
     * @throws GitlabException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public Project createRepository(String repositoryName, String description) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        Project project;
        try {
            project = projectApi.createProject(repositoryName);
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
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
                .withAuthorName("admin");
        try {
            commitsApi.createCommit(project.getId(), commitPayload);
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
        }


        return project;
    }

    /**
     * {@inheritDoc}
     *
     * @param repositoryId    идентификатор репозитория, который необходимо форкнуть
     * @param targetNamespace пространство имен, в котором создается форк
     * @throws GitlabException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public void forkRepository(Long repositoryId, String targetNamespace) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        try {
            projectApi.forkProject(repositoryId, targetNamespace);
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param newUserDto информация о новом пользователе
     * @throws GitlabException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public void createUser(NewUserDto newUserDto) {
        UserApi userApi = gitlabApi.getUserApi();
        User user = new User();
        user.setUsername(newUserDto.getUsername());
        user.setEmail(newUserDto.getEmail());
        user.setName(newUserDto.getName());
        try {
            userApi.createUser(user, "SimplePass123#", false);
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param projectId идентификатор проекта
     * @return true, если проект был форкнут, иначе false
     * @throws GitlabException если произошла ошибка при взаимодействии с GitLab API
     */
    @Override
    public boolean isForkedRepository(Long projectId) {
        ProjectApi projectApi = gitlabApi.getProjectApi();
        Project project;
        try {
            project = projectApi.getProject(projectId);
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
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
            throw new GitlabException(e.getMessage());
        }
    }


    /**
     * Добавление системного хука для обработки событий GitLab.
     *
     * @throws GitlabException если произошла ошибка при взаимодействии с GitLab API
     */
    @PostConstruct
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
                systemHooksApi.addSystemHook(HOOK_URL, hookToken, systemHook);
            }
        } catch (GitLabApiException e) {
            throw new GitlabException(e.getMessage());
        }
    }
}
