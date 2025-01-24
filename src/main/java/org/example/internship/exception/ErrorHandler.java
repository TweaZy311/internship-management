package org.example.internship.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> onAnyApiException(Exception ex) {
        log(ex);
        ExceptionResponse responseBody;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ResponseStatusException) {
            responseBody = ExceptionResponse.builder()
                    .message(((ResponseStatusException) ex).getReason())
                    .build();
            httpStatus = ((ResponseStatusException) ex).getStatus();
        }
        else if (ex instanceof ServiceException) {
            responseBody = ExceptionResponse.builder()
                    .message(((ServiceException) ex).getReason())
                    .code(((ServiceException) ex).getCode())
                    .build();
            httpStatus = ((ServiceException) ex).getStatus();
        } else {
            responseBody = ExceptionResponse.builder()
                    .message(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(responseBody, httpStatus);
    }

    private void log(Exception ex) {
        try {
            log.error(ex.toString(), ex);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
