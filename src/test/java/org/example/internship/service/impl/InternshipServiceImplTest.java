package org.example.internship.service.impl;

import org.example.internship.dto.request.internship.InternshipStatusDto;
import org.example.internship.dto.request.internship.NewInternshipDto;
import org.example.internship.dto.request.internship.UpdateInternshipDto;
import org.example.internship.dto.response.ReportDto;
import org.example.internship.dto.response.internship.AdminInternshipDto;
import org.example.internship.dto.response.internship.PublicInternshipDto;
import org.example.internship.mapper.InternshipMapper;
import org.example.internship.model.internship.Internship;
import org.example.internship.model.internship.InternshipStatus;
import org.example.internship.model.task.Solution;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.Role;
import org.example.internship.model.user.User;
import org.example.internship.repository.InternshipRepository;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.repository.TaskRepository;
import org.example.internship.repository.UserRepository;
import org.example.internship.service.internship.InternshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipServiceImplTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    private NewInternshipDto newInternshipDto;
    private Internship internship;
    private PublicInternshipDto publicInternshipDto;
    private AdminInternshipDto adminInternshipDto;
    private UpdateInternshipDto updateInternshipDto;

    @BeforeEach
    void setUp() {
        newInternshipDto = new NewInternshipDto();
        newInternshipDto.setName("Test Internship");
        newInternshipDto.setDescription("Test Description");
        newInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(10));
        newInternshipDto.setStartDate(LocalDate.now().plusDays(20));
        newInternshipDto.setEndDate(LocalDate.now().plusDays(30));

        internship = Internship.builder()
                .id(1L)
                .name("Test Internship")
                .description("Test Description")
                .registrationStartDate(LocalDate.now().plusDays(5))
                .registrationEndDate(LocalDate.now().plusDays(10))
                .startDate(LocalDate.now().plusDays(20))
                .endDate(LocalDate.now().plusDays(30))
                .status(InternshipStatus.OPEN)
                .build();

        publicInternshipDto = new PublicInternshipDto();
        publicInternshipDto.setId(1L);
        publicInternshipDto.setName("Test Internship");
        publicInternshipDto.setDescription("Test Description");
        publicInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(10));
        publicInternshipDto.setStartDate(LocalDate.now().plusDays(20));
        publicInternshipDto.setEndDate(LocalDate.now().plusDays(30));

        adminInternshipDto = new AdminInternshipDto();
        adminInternshipDto.setId(1L);
        adminInternshipDto.setName("Test Internship");
        adminInternshipDto.setDescription("Test Description");
        adminInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(10));
        adminInternshipDto.setStartDate(LocalDate.now().plusDays(20));
        adminInternshipDto.setEndDate(LocalDate.now().plusDays(30));

        updateInternshipDto = new UpdateInternshipDto();
        updateInternshipDto.setId(1L);
        updateInternshipDto.setDescription("Updated Description");
        updateInternshipDto.setRegistrationStartDate(LocalDate.now().plusDays(10));
        updateInternshipDto.setRegistrationEndDate(LocalDate.now().plusDays(15));
        updateInternshipDto.setStartDate(LocalDate.now().plusDays(25));
        updateInternshipDto.setEndDate(LocalDate.now().plusDays(35));
    }

    @Test
    void save_saveNewInternship() {
        when(internshipMapper.newDtoToToModel(newInternshipDto)).thenReturn(internship);

        internshipService.save(newInternshipDto);

        verify(internshipRepository, times(1)).saveAndFlush(internship);
    }

    @Test
    void changeStatus_changeInternshipStatus() {
        InternshipStatusDto statusDto = new InternshipStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("CLOSED");

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.changeStatus(statusDto);

        verify(internshipRepository, times(1)).saveAndFlush(internship);
        assertEquals(InternshipStatus.CLOSED, internship.getStatus());
    }

    @Test
    void changeStatus_internshipNotFound_throwException() {
        InternshipStatusDto statusDto = new InternshipStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("CLOSED");

        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.changeStatus(statusDto));
    }

    @Test
    void update_updateInternship() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.update(updateInternshipDto);

        verify(internshipMapper, times(1)).updateDtoToModel(internship, updateInternshipDto);
        verify(internshipRepository, times(1)).saveAndFlush(internship);
    }

    @Test
    void update_internshipNotFound_throwException() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.update(updateInternshipDto));
    }

    @Test
    void getById_returnInternship() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(internshipMapper.modelToPublicDto(internship)).thenReturn(publicInternshipDto);

        PublicInternshipDto result = internshipService.getById(1L);

        assertEquals(publicInternshipDto, result);
    }

    @Test
    void getById_internshipNotFound_throwException() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.getById(1L));
    }

    @Test
    void getById_internshipIsClosed_throwException() {
        internship.setStatus(InternshipStatus.CLOSED);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        assertThrows(EntityNotFoundException.class, () -> internshipService.getById(1L));
    }

    @Test
    void getAll_returnListOfInternships() {
        when(internshipRepository.findAll()).thenReturn(List.of(internship));
        when(internshipMapper.modelToAdminDto(internship)).thenReturn(adminInternshipDto);

        List<AdminInternshipDto> result = internshipService.getAll();

        assertEquals(1, result.size());
        assertEquals(adminInternshipDto, result.get(0));
    }

    @Test
    void getOpened_returnListOfOpenedInternships() {
        when(internshipRepository.findByStatus(InternshipStatus.OPEN)).thenReturn(List.of(internship));
        when(internshipMapper.modelToPublicDto(internship)).thenReturn(publicInternshipDto);

        List<PublicInternshipDto> result = internshipService.getOpened();

        assertEquals(1, result.size());
        assertEquals(publicInternshipDto, result.get(0));
    }

    @Test
    void getByStatus_returnListOfInternshipsWithStatus() {
        internship.setStatus(InternshipStatus.CLOSED);
        when(internshipRepository.findByStatus(InternshipStatus.CLOSED)).thenReturn(List.of(internship));
        when(internshipMapper.modelToAdminDto(internship)).thenReturn(adminInternshipDto);

        List<AdminInternshipDto> result = internshipService.getByStatus("CLOSED");

        assertEquals(1, result.size());
        assertEquals(adminInternshipDto, result.get(0));
    }

    @Test
    void createReport_returnReportForInternship() {
        User user = User.builder().id(1L).username("test-user").role(Role.USER).build();
        Task task = Task.builder().id(1L).name("Test Task").build();
        Solution solution = Solution.builder().id(1L).task(task).user(user).status(SolutionStatus.SENT).build();

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(userRepository.findAllByInternshipIdAndRole(1L, Role.USER)).thenReturn(List.of(user));
        when(taskRepository.findAllByLesson_InternshipId(1L)).thenReturn(List.of(task));
        when(solutionRepository.findAllByUserAndTaskIn(user, List.of(task))).thenReturn(List.of(solution));

        List<ReportDto> report = internshipService.createReport(1L);

        assertEquals(1, report.size());
        assertEquals("test-user", report.get(0).getUsername());
        assertEquals("SENT", report.get(0).getTaskStatuses().get("Test Task"));
    }

    @Test
    void createReport_internshipNotFound_throwException() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.createReport(1L));
    }

    @Test
    void createReport_usersNotFound_throwException() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(userRepository.findAllByInternshipIdAndRole(1L, Role.USER)).thenReturn(List.of());
        when(taskRepository.findAllByLesson_InternshipId(1L)).thenReturn(List.of(Task.builder().build()));

        assertThrows(EntityNotFoundException.class, () -> internshipService.createReport(1L));
    }

    @Test
    void createReport_tasksNotFound_throwException() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(userRepository.findAllByInternshipIdAndRole(1L, Role.USER)).thenReturn(List.of(User.builder().build()));
        when(taskRepository.findAllByLesson_InternshipId(1L)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> internshipService.createReport(1L));
    }
}