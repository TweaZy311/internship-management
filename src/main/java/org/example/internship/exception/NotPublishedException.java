package org.example.internship.exception;

/**
 * Исключение, которое выбрасывается при попытке доступа к объекту, который не был опубликован.
 */
public class NotPublishedException extends RuntimeException{
    public NotPublishedException(String message) {
        super(message);
    }
}
