package org.example.internship.controller;

import org.example.internship.dto.request.internship.InternshipStatusDto;
import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.ReportDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.exception.ExceptionResponse;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {

    @Mock
    private InternshipService internshipService;

    @Mock
    private Validator validator;

    @InjectMocks
    private InternshipController internshipController;

    private NewInternshipDto newInternshipDto;
    private UpdateInternshipDto updateInternshipDto;
    private InternshipStatusDto statusDto;

    @BeforeEach
    void setUp() {
        newInternshipDto = new NewInternshipDto();
        newInternshipDto.setName("Test Internship");
        newInternshipDto.setDescription("Test Description");
        newInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(10));
        newInternshipDto.setStartDate(LocalDate.now().plusDays(20));
        newInternshipDto.setEndDate(LocalDate.now().plusDays(30));

        updateInternshipDto = new UpdateInternshipDto();
        updateInternshipDto.setId(1L);
        updateInternshipDto.setDescription("Updated Description");
        updateInternshipDto.setRegistrationStartDate(LocalDate.now().plusDays(10));
        updateInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(20));
        updateInternshipDto.setStartDate(LocalDate.now().plusDays(30));
        updateInternshipDto.setEndDate(LocalDate.now().plusDays(40));

        statusDto = new InternshipStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("CLOSED");
    }

    @Test
    void createInternship_returnCreated() {
        when(validator.dateIsValid(newInternshipDto.getStartDate(),
                newInternshipDto.getEndDate(),
                newInternshipDto.getRegistrationEndDate())).thenReturn(true);

        ResponseEntity<ExceptionResponse> response = internshipController.createInternship(newInternshipDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(internshipService, times(1)).save(newInternshipDto);
    }

    @Test
    void createInternship_invalidDates_returnBadRequest() {
        when(validator.dateIsValid(newInternshipDto.getStartDate(),
                newInternshipDto.getEndDate(),
                newInternshipDto.getRegistrationEndDate())).thenReturn(false);

        ResponseEntity<ExceptionResponse> response = internshipController.createInternship(newInternshipDto);

        verify(internshipService, never()).save(newInternshipDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong date input", response.getBody().getMessage());
    }

    @Test
    void changeInternshipStatus_returnOk() {
        ResponseEntity<Void> response = internshipController.changeInternshipStatus(statusDto);

        verify(internshipService, times(1)).changeStatus(statusDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllInternships_returnListOfInternships() {
        List<AdminInternshipDto> internships = List.of(new AdminInternshipDto());

        when(internshipService.getAll()).thenReturn(internships);

        ResponseEntity<List<AdminInternshipDto>> response = internshipController.getAllInternships(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internships, response.getBody());
    }

    @Test
    void getAllInternships_emptyList_returnNoContent() {
        when(internshipService.getAll()).thenReturn(List.of());

        ResponseEntity<List<AdminInternshipDto>> response = internshipController.getAllInternships(null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAllInternships_withStatus_returnListOfInternshipsWithStatus() {
        List<AdminInternshipDto> internshipDtos = List.of(new AdminInternshipDto());
        when(internshipService.getByStatus("CLOSED")).thenReturn(internshipDtos);

        ResponseEntity<List<AdminInternshipDto>> response = internshipController.getAllInternships("CLOSED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDtos, response.getBody());
    }

    @Test
    void getAllOpenedInternships_returnListOfOpenedInternships() {
        List<PublicInternshipDto> internshipDtos = List.of(new PublicInternshipDto());
        when(internshipService.getOpened()).thenReturn(internshipDtos);

        ResponseEntity<List<PublicInternshipDto>> response = internshipController.getAllOpenedInternships();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDtos, response.getBody());
    }

    @Test
    void getAllOpenedInternships_emptyList_returnNoContent() {
        when(internshipService.getOpened()).thenReturn(List.of());

        ResponseEntity<List<PublicInternshipDto>> response = internshipController.getAllOpenedInternships();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getInternshipById_returnInternship() {
        PublicInternshipDto internshipDto = new PublicInternshipDto();
        when(internshipService.getById(1L)).thenReturn(internshipDto);

        ResponseEntity<PublicInternshipDto> response = internshipController.getInternshipById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDto, response.getBody());
    }

    @Test
    void updateInternship_returnOk() {
        when(validator.dateIsValid(updateInternshipDto.getStartDate(),
                updateInternshipDto.getEndDate(),
                updateInternshipDto.getRegistrationStartDate(),
                updateInternshipDto.getRegistrationEndDate())).thenReturn(true);

        ResponseEntity<ExceptionResponse> response = internshipController.updateInternship(updateInternshipDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(internshipService, times(1)).update(updateInternshipDto);
    }

    @Test
    void updateInternship_invalidDates_returnBadRequest() {
        when(validator.dateIsValid(updateInternshipDto.getStartDate(),
                updateInternshipDto.getEndDate(),
                updateInternshipDto.getRegistrationStartDate(),
                updateInternshipDto.getRegistrationEndDate())).thenReturn(false);

        ResponseEntity<ExceptionResponse> response = internshipController.updateInternship(updateInternshipDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wrong date input", response.getBody().getMessage());
    }

    @Test
    void getReport_returnReport() {
        List<ReportDto> reportDtos = List.of(new ReportDto("user1", Map.of()));
        when(internshipService.createReport(1L)).thenReturn(reportDtos);

        ResponseEntity<List<ReportDto>> response = internshipController.getReport(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reportDtos, response.getBody());
    }
}