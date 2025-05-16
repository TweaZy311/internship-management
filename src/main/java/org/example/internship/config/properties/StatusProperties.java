package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "status")
@Validated
@Getter
@Setter
public class StatusProperties {
    private Long defaultApplicationStatusId;
    private Long defaultSolutionStatusId;
}
