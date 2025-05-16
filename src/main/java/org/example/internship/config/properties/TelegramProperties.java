package org.example.internship.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Validated
@Getter
@Setter
public class TelegramProperties {
    private Boolean enabled = Boolean.TRUE;
    private String token = "7743770450:AAGhUjjxXp39NzN4PMk9_ZqJXJkJ2jkMXAI";
    private String username = "gandon";
}
