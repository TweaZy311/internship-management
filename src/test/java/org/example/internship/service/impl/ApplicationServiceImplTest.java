package org.example.internship.service.impl;

import org.example.internship.dto.request.application.ApplicationStatusDto;
import org.example.internship.dto.request.application.NewApplicationDto;
import org.example.internship.dto.response.application.ApplicationDto;
import org.example.internship.mapper.ApplicationMapper;
import org.example.internship.model.application.Application;
import org.example.internship.model.application.ApplicationStatus;
import org.example.internship.model.application.EducationStatus;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.internship.InternshipStatus;
import org.example.internship.repository.ApplicationRepository;
import org.example.internship.service.application.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private NewApplicationDto newApplicationDto;
    private Application application;
    private ApplicationDto applicationDto;
    private Internship internship;

    @BeforeEach
    void setUp() {
        internship = Internship.builder()
                .id(1L)
                .endDate(LocalDate.now().plusDays(1))
                .registrationStartDate(LocalDate.now().minusDays(5))
                .registrationEndDate(LocalDate.now().minusDays(2))
                .status(InternshipStatus.OPEN)
                .build();
        newApplicationDto = new NewApplicationDto();
        newApplicationDto.setFullName("Test User");
        newApplicationDto.setEmail("test@test.com");
        newApplicationDto.setPhoneNumber("+79991112233");
        newApplicationDto.setUsername("test-user");
        newApplicationDto.setTelegramId("@id");
        newApplicationDto.setBirthDate(LocalDate.now().minusYears(20));
        newApplicationDto.setCity("Test City");
        newApplicationDto.setEducationStatus("student");
        newApplicationDto.setInternshipId(1L);

        application = Application.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@test.com")
                .phoneNumber("+79991112233")
                .username("test-user")
                .telegramId("@id")
                .birthDate(LocalDate.now().minusYears(20))
                .city("Test City")
                .educationStatus(EducationStatus.STUDENT)
                .status(ApplicationStatus.SENT)
                .internship(internship)
                .creationDate(LocalDate.now())
                .build();

        applicationDto = new ApplicationDto();
        applicationDto.setId(1L);
        applicationDto.setFullName("Test User");
        applicationDto.setEmail("test@test.com");
        applicationDto.setPhoneNumber("+79991112233");
        applicationDto.setUsername("test-user");
        applicationDto.setTelegramId("@id");
        applicationDto.setBirthDate(LocalDate.now().minusYears(20));
        applicationDto.setCity("Test City");
        applicationDto.setEducationStatus(EducationStatus.STUDENT);
        applicationDto.setInternshipId(1L);
        applicationDto.setCreationDate(LocalDate.now());
    }

    @Test
    void save_saveNewApplication() {
        when(applicationRepository.findByPhoneNumberAndInternshipId(
                newApplicationDto.getPhoneNumber(), newApplicationDto.getInternshipId())).thenReturn(null);
        when(applicationMapper.toModel(newApplicationDto)).thenReturn(application);

        applicationService.save(newApplicationDto);

        verify(applicationRepository, times(1)).saveAndFlush(application);
    }

    @Test
    void save_applicationAlreadyExists_throwException() {
        internship.setRegistrationStartDate(LocalDate.now());
        application.setInternship(internship);

        when(applicationRepository.findByPhoneNumberAndInternshipId(
                newApplicationDto.getPhoneNumber(), newApplicationDto.getInternshipId())).thenReturn(application);

        assertThrows(EntityExistsException.class, () -> applicationService.save(newApplicationDto));
    }

    @Test
    void save_applicationAlreadyExistsAndInternshipOpensOneMoreTime_updateApplication() {
        internship.setRegistrationStartDate(LocalDate.now().plusDays(1));
        application.setInternship(internship);

        when(applicationRepository.findByPhoneNumberAndInternshipId(
                newApplicationDto.getPhoneNumber(), newApplicationDto.getInternshipId())).thenReturn(application);
        when(applicationMapper.toModel(newApplicationDto)).thenReturn(application);

        applicationService.save(newApplicationDto);

        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void save_applicationAlreadyExistAndInternshipIsClosed_throwException() {
        internship.setStatus(InternshipStatus.CLOSED);
        application.setInternship(internship);

        when(applicationRepository.findByPhoneNumberAndInternshipId(
                newApplicationDto.getPhoneNumber(), newApplicationDto.getInternshipId())).thenReturn(application);

        assertThrows(EntityExistsException.class, () -> applicationService.save(newApplicationDto));
    }

    @Test
    void changeStatus_changeApplicationStatus() {
        ApplicationStatusDto statusDto = new ApplicationStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("APPROVED");

        when(applicationRepository.findById(statusDto.getId())).thenReturn(Optional.of(application));

        applicationService.changeStatus(statusDto);

        verify(applicationRepository, times(1)).saveAndFlush(application);
        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
    }

    @Test
    void changeStatus_applicationNotFound_throwException() {
        ApplicationStatusDto statusDto = new ApplicationStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("APPROVED");

        when(applicationRepository.findById(statusDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> applicationService.changeStatus(statusDto));
    }

    @Test
    void getAll_returnListOfApplications() {
        when(applicationRepository.findAll()).thenReturn(List.of(application));
        when(applicationMapper.toDto(application)).thenReturn(applicationDto);

        List<ApplicationDto> result = applicationService.getAll();

        assertEquals(1, result.size());
        assertEquals(applicationDto, result.get(0));
    }

    @Test
    void getById_returnApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationMapper.toDto(application)).thenReturn(applicationDto);

        ApplicationDto result = applicationService.getById(1L);

        assertEquals(applicationDto, result);
    }

    @Test
    void getById_applicationNotFound_throwException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> applicationService.getById(1L));
    }

    @Test
    void getByStatus_returnListOfApplicationsWithStatus() {
        when(applicationRepository.findAllByStatus(ApplicationStatus.SENT)).thenReturn(List.of(application));
        when(applicationMapper.toDto(application)).thenReturn(applicationDto);

        List<ApplicationDto> result = applicationService.getByStatus("SENT");

        assertEquals(1, result.size());
        assertEquals(applicationDto, result.get(0));
    }

    @Test
    void getAllByInternshipId_returnListOfApplications() {
        when(applicationRepository.findAllByInternshipId(internship.getId())).thenReturn(List.of(application));
        when(applicationMapper.toDto(application)).thenReturn(applicationDto);

        List<ApplicationDto> result = applicationService.getAllByInternshipId(1L);

        assertEquals(1, result.size());
        assertEquals(applicationDto, result.get(0));
    }
}