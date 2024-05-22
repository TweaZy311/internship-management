package org.example.internship.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация Spring Security для приложения.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final AuthEntryPoint authEntryPoint;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(AuthEntryPoint authEntryPoint, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.authEntryPoint = authEntryPoint;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Настройка HTTP безопасности при помощи Basic Authentication.
     *
     * @param http билдер для настройки безопасности HTTP
     * @throws Exception если возникла ошибка при настройке безопасности
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/application/create",
                        "/api/internship/opened",
                        "/api/internship/{id}",
                        "/api/solution/add",
                        "/api-docs",
                        "/swagger-ui/**",
                        "/v3/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(authEntryPoint)
                .and()
                .sessionManagement().disable();
    }

    /**
     * Настройка аутентификации с использованием UserDetailsService.
     *
     * @param auth билдер для настройки аутентификации
     * @throws Exception если возникла ошибка при настройке аутентификации
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
