package org.example.internship;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ContextConfiguration(classes = InternshipApplication.class, initializers = {InternshipApplicationTests.Initializer.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternshipApplicationTests {
    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @ClassRule
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("internship")
            .withUsername("internship")
            .withPassword("internship");


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            if (!POSTGRES_CONTAINER.isRunning()) {
                POSTGRES_CONTAINER.start();
            }

            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
            ).applyTo(context.getEnvironment());
        }
    }

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }


    @SneakyThrows
    protected String readResourceFile(String path) {
        Resource resource = new ClassPathResource(path);
        return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
    }

}
