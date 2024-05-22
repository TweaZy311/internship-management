package org.example.internship.service.gitlab;

import org.example.internship.dto.request.NewUserDto;
import org.gitlab4j.api.models.Project;

/**
 * Сервис для взаимодействия с GitLab.
 */
public interface GitlabService {

    /**
     * Создание нового репозитория в GitLab.
     *
     * @param repositoryName название репозитория
     * @param description    описание репозитория
     * @return объект Project, представляющий созданный репозиторий
     */
    Project createRepository(String repositoryName, String description);

    /**
     * Форк репозитория другому пользователю.
     *
     * @param repositoryId    идентификатор репозитория
     * @param targetNamespace пространство имен целевого пользователя
     */
    void forkRepository(Long repositoryId, String targetNamespace);

    /**
     * Создание нового пользователя в GitLab.
     *
     * @param newUserDto информация о новом пользователе
     */
    void createUser(NewUserDto newUserDto);

    /**
     * Проверка, был ли репозиторий клонирован.
     *
     * @param projectId идентификатор проекта
     * @return true, если репозиторий был клонирован, иначе false
     */
    boolean isForkedRepository(Long projectId);

    /**
     * Блокировка пользователя по username в случае отчисления с курса
     *
     * @param username имя пользователя
     */
    void blockUser(String username);

}
