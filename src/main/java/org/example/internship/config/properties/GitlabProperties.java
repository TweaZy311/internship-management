package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "gitlab")
@Validated
@Getter
@Setter
public class GitlabProperties {
    @NotBlank(message = "gitlab.systemHookToken property cant be blank")
    private String systemHookToken;
    @NotBlank(message = "gitlab.url property cant be blank")
    private String url;
    @NotBlank(message = "gitlab.personalAccessToken property cant be blank")
    private String personalAccessToken;
    @NotBlank(message = "gitlab.userPassword property cant be blank")
    private String userPassword;
}
