package org.example.internship.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки соответствия ID пользователя в запросе ID аутентифицированного пользователя.
 * <br>
 * Эта аннотация используется для ограничения доступа к методам контроллера,
 * чтобы только пользователь с совпадающим ID мог выполнить запрос.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserIdMatches {
}
