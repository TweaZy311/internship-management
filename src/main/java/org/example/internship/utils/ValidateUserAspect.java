package org.example.internship.utils;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.internship.annotation.UserIdMatches;
import org.example.internship.annotation.UsernameMatches;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;


/**
 * Аспект для проверки соответствия ID или имени пользователя в запросе ID или имени аутентифицированного пользователя.
 * <br>
 * Применяется для методов, помеченных аннотацией {@link UserIdMatches} и проверяет,
 * совпадает ли ID пользователя в запросе с ID аутентифицированного пользователя, а также аннотацией
 * {@link UsernameMatches}, и проверяет, совпадает ли имя пользователя в запросе с именем аутентифицированного пользователя.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ValidateUserAspect {
    private final UserRepository userRepository;

    /**
     * Метод, проверяющий соответствие ID пользователя перед вызовом метода контроллера.
     *
     * @throws ResponseStatusException если ID пользователя в запросе не совпадает с ID аутентифицированного пользователя.
     */
    @Before("@annotation(org.example.internship.annotation.UserIdMatches)")
    public void validateUserId() throws ResponseStatusException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long id = Long.parseLong(request.getParameter("id"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (!id.equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User ID mismatch");
        }
    }

    /**
     * Метод, проверяющий соответствие имени пользователя перед вызовом метода контроллера.
     *
     * @throws ResponseStatusException если имя пользователя в запросе не совпадает с именем аутентифицированного пользователя
     * или аутентифицированный пользователь не имеет роли администратора.
     */
    @Before("@annotation(org.example.internship.annotation.UsernameMatches)")
    public void validateUsername() throws ResponseStatusException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String usernameInParam = request.getParameter("username");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (!user.getRole().equals(Role.ADMIN) && (usernameInParam == null || usernameInParam.isEmpty()
                || !usernameInParam.equals(username))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Username mismatch");
        }
    }
}
