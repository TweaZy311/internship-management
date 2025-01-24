package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "admin")
@Validated
@Getter
@Setter
public class AdminProperties {
    @NotBlank(message = "admin.username property cant be blank")
    private String username;
    @NotBlank(message = "admin.name property cant be blank")
    private String name;
    @NotBlank(message = "admin.email property cant be blank")
    private String email;
    @NotBlank(message = "admin.password property cant be blank")
    private String password;
}
