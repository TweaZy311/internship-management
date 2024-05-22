package org.example.internship.controller;

import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.exception.ExceptionResponse;
import org.example.internship.service.application.ApplicationService;
import org.example.internship.service.internship.InternshipService;
import org.example.internship.utils.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private InternshipService internshipService;

    @Mock
    private Validator validator;

    @InjectMocks
    private ApplicationController applicationController;

    private NewApplicationDto validApplicationDto;
    private NewApplicationDto invalidEmailApplicationDto;
    private NewApplicationDto invalidPhoneApplicationDto;
    private PublicInternshipDto openInternshipDto;

    @BeforeEach
    void setUp() {
        validApplicationDto = new NewApplicationDto();
        validApplicationDto.setEmail("valid@email.com");
        validApplicationDto.setPhoneNumber("+79123456789");
        validApplicationDto.setInternshipId(1L);

        invalidEmailApplicationDto = new NewApplicationDto();
        invalidEmailApplicationDto.setEmail("invalid_email");
        invalidEmailApplicationDto.setPhoneNumber("+79123456789");
        invalidEmailApplicationDto.setInternshipId(1L);

        invalidPhoneApplicationDto = new NewApplicationDto();
        invalidPhoneApplicationDto.setEmail("valid@email.com");
        invalidPhoneApplicationDto.setPhoneNumber("invalid_phone");
        invalidPhoneApplicationDto.setInternshipId(1L);

        openInternshipDto = new PublicInternshipDto();
        openInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(1));
    }

    @Test
    void createApplication_validData_returnCreated() {
        when(internshipService.getById(1L)).thenReturn(openInternshipDto);
        when(validator.emailIsValid(validApplicationDto.getEmail())).thenReturn(true);
        when(validator.phoneNumberIsValid(validApplicationDto.getPhoneNumber())).thenReturn(true);

        ResponseEntity<ExceptionResponse> response = applicationController.createApplication(validApplicationDto);

        verify(applicationService, times(1)).save(validApplicationDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createApplication_invalidEmail_returnBadRequest() {
        when(internshipService.getById(1L)).thenReturn(openInternshipDto);
        when(validator.emailIsValid(invalidEmailApplicationDto.getEmail())).thenReturn(false);

        ResponseEntity<ExceptionResponse> response = applicationController.createApplication(invalidEmailApplicationDto);

        verify(applicationService, never()).save(any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong e-mail format", response.getBody().getMessage());
    }

    @Test
    void createApplication_invalidPhone_returnBadRequest() {
        when(internshipService.getById(1L)).thenReturn(openInternshipDto);
        when(validator.emailIsValid(invalidPhoneApplicationDto.getEmail())).thenReturn(true);
        when(validator.phoneNumberIsValid(invalidPhoneApplicationDto.getPhoneNumber())).thenReturn(false);

        ResponseEntity<ExceptionResponse> response = applicationController.createApplication(invalidPhoneApplicationDto);

        verify(applicationService, never()).save(any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong phone number format", response.getBody().getMessage());
    }

    @Test
    void changeApplicationStatus_returnOk() {
        ApplicationStatusDto statusDto = new ApplicationStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("APPROVED");

        ResponseEntity<Void> response = applicationController.changeApplicationStatus(statusDto);

        verify(applicationService, times(1)).changeStatus(statusDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllApplications_returnListOfApplications() {
        List<ApplicationDto> applications = List.of(new ApplicationDto());
        when(applicationService.getAll()).thenReturn(applications);

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications.size(), response.getBody().size());
        assertEquals(applications.get(0), response.getBody().get(0));
    }

    @Test
    void getAllApplications_emptyList_returnNoContent() {
        when(applicationService.getAll()).thenReturn(List.of());

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications(null, null);

        verify(applicationService, times(1)).getAll();
        verify(applicationService, never()).getByStatus(any());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAllApplications_withStatus_returnListOfApplications() {
        List<ApplicationDto> applications = List.of(new ApplicationDto());
        when(applicationService.getByStatus("approved")).thenReturn(applications);

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications("approved", null);

        verify(applicationService, never()).getAll();
        verify(applicationService, times(1)).getByStatus("approved");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications.size(), response.getBody().size());
        assertEquals(applications.get(0), response.getBody().get(0));
    }

    @Test
    void getAllApplications_withInternshipId_returnListOfApplications() {
        List<ApplicationDto> applications = List.of(new ApplicationDto());
        when(applicationService.getAllByInternshipId(1L)).thenReturn(applications);

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications(null,1L);

        verify(applicationService, never()).getAll();
        verify(applicationService, times(1)).getAllByInternshipId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications.size(), response.getBody().size());
        assertEquals(applications.get(0), response.getBody().get(0));
    }

    @Test
    void getAllApplications_withBothParams_returnListOfApplications() {
        List<ApplicationDto> applications = List.of(new ApplicationDto());
        when(applicationService.getAllByInternshipIdAndStatus(1L, "APPROVED")).thenReturn(applications);

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications("APPROVED",1L);

        verify(applicationService, never()).getAll();
        verify(applicationService, times(1)).getAllByInternshipIdAndStatus(1L, "APPROVED");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications.size(), response.getBody().size());
        assertEquals(applications.get(0), response.getBody().get(0));
    }

    @Test
    void getApplicationById_returnOk() {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setId(1L);
        when(applicationService.getById(1L)).thenReturn(applicationDto);

        ResponseEntity<ApplicationDto> response = applicationController.getApplicationById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationDto, response.getBody());
    }

}