package org.example.internship.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ServiceException extends RuntimeException {
    private final int status;
    private final String code;
    private final String reason;

    public ServiceException(HttpStatus status, String code, String reason) {
        this.status = status.value();
        this.code = code;
        this.reason = reason;
    }

    public ServiceException(Exception e, String code) {
        super(e);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.code = code;
        this.reason = e.getMessage();
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(this.status);
    }
}
