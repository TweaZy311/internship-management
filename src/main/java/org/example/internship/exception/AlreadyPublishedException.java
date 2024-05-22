package org.example.internship.exception;

/**
 * Исключение, которое выбрасывается при попытке повторной публикации объекта, который уже был опубликован.
 */
public class AlreadyPublishedException extends RuntimeException {
    public AlreadyPublishedException(String message) {
        super(message);
    }
}
