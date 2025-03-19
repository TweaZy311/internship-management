package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "status")
@Validated
@Getter
@Setter
public class StatusProperties {
    @NotNull(message = "status.defaultApplicationStatusId property cant be blank")
    private Long defaultApplicationStatusId;
    @NotNull(message = "status.defaultSolutionStatusId property cant be blank")
    private Long defaultSolutionStatusId;
}
