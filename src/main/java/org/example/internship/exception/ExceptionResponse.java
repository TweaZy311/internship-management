package org.example.internship.exception;

import lombok.*;

/**
 * Класс, представляющий объект ответа на исключение.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionResponse {
    private String message;
    private String code;
}
