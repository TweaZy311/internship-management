package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "token")
@Validated
@Getter
@Setter
public class TokenProperties {
    @NotNull(message = "token.accessExpiration property cant be blank")
    private int accessExpiration;
    @NotNull(message = "token.refreshExpiration property cant be blank")
    private int refreshExpiration;
}
