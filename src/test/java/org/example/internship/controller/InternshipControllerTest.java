package org.example.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.internship.InternshipApplicationTests;
import org.example.internship.entity.*;
import org.example.internship.model.BooleanOperator;
import org.example.internship.model.SearchCriteria;
import org.example.internship.model.SearchKey;
import org.example.internship.model.SearchOperation;
import org.example.internship.model.request.GenerateTokenRequest;
import org.example.internship.model.request.BaseGetListRequest;
import org.example.internship.model.request.internship.CreateUpdateInternshipRequest;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.StatusRepository;
import org.example.internship.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InternshipControllerTest extends InternshipApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InternshipRepository internshipRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private Long statusId;

    private String adminToken;
    private Long internshipId;

    @BeforeEach
    void setup() throws Exception {
        internshipRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity admin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .email("admin@example.com")
                .name("Admin")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(admin);

        StatusEntity status = StatusEntity.builder()
                .name("Active")
                .type(StatusType.INTERNSHIP)
                .build();
        statusId = statusRepository.save(status).getId();

        GenerateTokenRequest authRequest = new GenerateTokenRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("adminpass");

        String json = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = "Bearer " + objectMapper.readTree(json).get("accessToken").asText();

        InternshipEntity internship = InternshipEntity.builder()
                .name("Test Internship")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .registrationStartDate(LocalDate.now().minusDays(5))
                .registrationEndDate(LocalDate.now().plusDays(5))
                .isOpen(true)
                .build();
        internshipId = internshipRepository.save(internship).getId();
    }

    @Test
    void shouldCreateInternshipSuccessfully() throws Exception {
        CreateUpdateInternshipRequest request = new CreateUpdateInternshipRequest();
        request.setName("New Internship");
        request.setStartDate(LocalDate.now().plusDays(10));
        request.setEndDate(LocalDate.now().plusDays(30));
        request.setRegistrationStartDate(LocalDate.now());
        request.setRegistrationEndDate(LocalDate.now().plusDays(5));
        request.setStatusId(statusId);

        mockMvc.perform(post("/api/internship/create")
                        .header("Authorization", adminToken)
                        .header("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Internship"));
    }

    @Test
    void shouldRejectInvalidDatesWhenCreatingInternship() throws Exception {
        CreateUpdateInternshipRequest request = new CreateUpdateInternshipRequest();
        request.setName("Invalid Internship");
        request.setStartDate(LocalDate.now().plusDays(10));
        request.setEndDate(LocalDate.now().plusDays(5)); // end before start
        request.setRegistrationStartDate(LocalDate.now());
        request.setRegistrationEndDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/internship/create")
                        .header("Authorization", adminToken)
                        .header("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Wrong date input"));
    }

    @Test
    void shouldGetInternshipById() throws Exception {
        mockMvc.perform(get("/api/internship/{id}", internshipId)
                        .param("private", "false")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Internship"));
    }

    @Test
    void shouldUpdateInternship() throws Exception {
        CreateUpdateInternshipRequest updateRequest = new CreateUpdateInternshipRequest();
        updateRequest.setName("Updated Internship");
        updateRequest.setStartDate(LocalDate.now().plusDays(10));
        updateRequest.setEndDate(LocalDate.now().plusDays(30));
        updateRequest.setRegistrationStartDate(LocalDate.now());
        updateRequest.setRegistrationEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(patch("/api/internship/update/{id}", internshipId)
                        .header("Authorization", adminToken)
                        .header("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Internship"));
    }

    @Test
    void shouldGetInternshipPageAsAdmin() throws Exception {
        BaseGetListRequest request = new BaseGetListRequest();
        request.setPage(0);
        request.setPageSize(10);
        request.setFilters(Collections.emptyList());

        mockMvc.perform(post("/api/internship/page")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldFilterInternshipsByStatusName() throws Exception {
        StatusEntity anotherStatus = statusRepository.save(StatusEntity.builder()
                .name("Inactive")
                .type(StatusType.INTERNSHIP)
                .build());

        InternshipEntity secondInternship = InternshipEntity.builder()
                .name("Filtered Internship")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(20))
                .registrationStartDate(LocalDate.now().minusDays(2))
                .registrationEndDate(LocalDate.now().plusDays(2))
                .isOpen(true)
                .status(anotherStatus)
                .build();
        internshipRepository.save(secondInternship);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKey(SearchKey.STATUS_NAME);
        criteria.setOperation(SearchOperation.EQ);
        criteria.setValue(anotherStatus.getName());
        criteria.setOperator(BooleanOperator.AND);
        BaseGetListRequest request = new BaseGetListRequest();
        request.setPage(0);
        request.setPageSize(10);
        request.setFilters(List.of(criteria));

        mockMvc.perform(post("/api/internship/page")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Filtered Internship"));
    }


    @Test
    void shouldGetInternshipReport() throws Exception {
        mockMvc.perform(get("/api/internship/{id}/report", internshipId)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
