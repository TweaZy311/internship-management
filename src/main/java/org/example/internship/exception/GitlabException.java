package org.example.internship.exception;

/**
 * Исключение, которое выбрасывается при возникновении ошибок взаимодействия с GitLab.
 */
public class GitlabException extends RuntimeException {
    public GitlabException(String message) {
        super(message);
    }
}
