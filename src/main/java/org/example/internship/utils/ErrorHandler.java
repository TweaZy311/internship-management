package org.example.internship.utils;


import org.example.internship.exception.AlreadyPublishedException;
import org.example.internship.exception.ExceptionResponse;
import org.example.internship.exception.GitlabException;
import org.example.internship.exception.NotPublishedException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обработка исключения, когда запрашиваемый ресурс не найден.
     *
     * @param e исключение типа EntityNotFoundException.
     * @return ответ с кодом состояния 404 NOT_FOUND и сообщением об ошибке.
     */
    @ExceptionHandler({
            EntityNotFoundException.class,
    })
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка исключений, связанных с нарушением целостности данных в базе.
     *
     * @param e Исключение типа ConstraintViolationException, DataIntegrityViolationException
     *          или EntityExistsException.
     * @return Ответ с кодом состояния 409 CONFLICT и сообщением об ошибке.
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            DataIntegrityViolationException.class,
            EntityExistsException.class,
    })
    public ResponseEntity<ExceptionResponse> handleDatabaseException(RuntimeException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Обработка исключения, возникающего при недопустимых аргументах метода.
     *
     * @param e Исключение типа IllegalArgumentException.
     * @return Ответ с кодом состояния 400 BAD_REQUEST и сообщением об ошибке.
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
    })
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения, связанного с ошибками в GitLab.
     *
     * @param e Исключение типа GitlabException.
     * @return Ответ с кодом состояния 400 BAD_REQUEST и сообщением об ошибке.
     */
    @ExceptionHandler({
            GitlabException.class,
    })
    public ResponseEntity<ExceptionResponse> handleGitLabException(GitlabException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключений, возникающих при попытке повторной публикации уже опубликованных данных.
     *
     * @param e Исключение типа AlreadyPublishedException или NotPublishedException.
     * @return Ответ с кодом состояния 409 CONFLICT и сообщением об ошибке.
     */
    @ExceptionHandler({
            AlreadyPublishedException.class,
            NotPublishedException.class
    })
    public ResponseEntity<ExceptionResponse> handlePublishException(RuntimeException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.CONFLICT);
    }
}
