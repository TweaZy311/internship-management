package org.example.internship.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.internship.annotation.GitlabTokenRequired;
import org.example.internship.config.properties.GitlabProperties;
import org.example.internship.exception.ErrorCode;
import org.example.internship.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Аспект, который выполняет проверку наличия токена GitLab.
 * <br>
 * Применяется для методов, помеченных аннотацией {@link GitlabTokenRequired}, и выполняет проверку наличия
 * корректного токена GitLab в заголовке запроса (X-Gitlab-Token).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ValidateTokenAspect {
    private final GitlabProperties gitlabProperties;

    /**
     * Метод, выполняющий проверку токена перед выполнением метода, помеченного аннотацией GitlabTokenRequired.
     *
     * @throws ServiceException если токен не прошел валидацию
     */
    @Before("@annotation(org.example.internship.annotation.GitlabTokenRequired)")
    public void validateToken() throws ServiceException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("X-Gitlab-Token");
        if (StringUtils.isEmpty(token) || !token.equals(gitlabProperties.getSystemHookToken()) ) {
            throw new ServiceException(HttpStatus.FORBIDDEN, ErrorCode.GLB_403.getCode(), "Invalid token");
        }
    }
}
