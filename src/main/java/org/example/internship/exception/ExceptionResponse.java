package org.example.internship.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий объект ответа на исключение.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private String message;
}
