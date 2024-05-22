package org.example.internship.service.impl;

import org.example.internship.dto.request.solution.SolutionStatusDto;
import org.example.internship.dto.response.solution.SolutionDto;
import org.example.internship.mapper.SolutionMapper;
import org.example.internship.model.task.Solution;
import org.example.internship.model.task.SolutionStatus;
import org.example.internship.model.task.Task;
import org.example.internship.model.user.User;
import org.example.internship.repository.SolutionRepository;
import org.example.internship.service.solution.SolutionServiceImpl;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceImplTest {

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private SolutionMapper solutionMapper;

    @InjectMocks
    private SolutionServiceImpl solutionService;

    private PushSystemHookEvent pushEvent;
    private Solution solution;
    private SolutionDto solutionDto;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("test-user").build();
        Task task = Task.builder().id(1L).name("test-task").build();

        Date commitTime = new Date();
        LocalDateTime formattedCommitTime = commitTime.toInstant()
                .atZone(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime();

        EventCommit eventCommit = new EventCommit();
        eventCommit.setTimestamp(commitTime);
        eventCommit.setUrl("https://test.com/commit");

        EventProject project = new EventProject();
        project.setWebUrl("https://test.com/project");
        project.setName("test-task");

        pushEvent = new PushSystemHookEvent();
        pushEvent.setProject(project);
        pushEvent.setUserUsername("test-user");
        pushEvent.setCommits(List.of(eventCommit));

        solution = Solution.builder()
                .id(1L)
                .repositoryUrl("https://giltab.localhost.com/project")
                .lastCommitTime(formattedCommitTime)
                .lastCommitUrl("https://gitlab.localhost.com/commit")
                .user(user)
                .task(task)
                .status(SolutionStatus.SENT)
                .build();

        solutionDto = new SolutionDto();
        solutionDto.setId(1L);
        solutionDto.setRepositoryUrl("https://gitlab.localhost.com/project");
        solutionDto.setLastCommitTime(formattedCommitTime);
        solutionDto.setLastCommitUrl("https://gitlab.localhost.com/commit");
        solutionDto.setUserId(1L);
        solutionDto.setTaskId(1L);
    }

//    @Test
//    void add_addNewSolution() {
//        when(solutionMapper.pushEventToModel(pushEvent)).thenReturn(solution);
//
//        solutionService.add(pushEvent);
//
//        verify(solutionRepository, times(1)).saveAndFlush(solution);
//    }

    @Test
    void updateStatus_updateSolutionStatus() {
        SolutionStatusDto statusDto = new SolutionStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("APPROVED");

        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));

        solutionService.updateStatus(statusDto);

        assertEquals(SolutionStatus.APPROVED, solution.getStatus());
        assertNotNull(solution.getCheckedTime());
        verify(solutionRepository, times(1)).save(solution);
    }

    @Test
    void updateStatus_solutionNotFound_throwException() {
        SolutionStatusDto statusDto = new SolutionStatusDto();
        statusDto.setId(1L);
        statusDto.setStatus("APPROVED");

        when(solutionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> solutionService.updateStatus(statusDto));
    }

    @Test
    void getById_returnSolution() {
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(solution));
        when(solutionMapper.modelToDto(solution)).thenReturn(solutionDto);

        SolutionDto result = solutionService.getById(1L);

        assertEquals(solutionDto, result);
    }

    @Test
    void getById_solutionNotFound_throwException() {
        when(solutionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> solutionService.getById(1L));
    }

    @Test
    void getAll_returnListOfSolutions() {
        when(solutionRepository.findAll()).thenReturn(List.of(solution));
        when(solutionMapper.modelToDto(solution)).thenReturn(solutionDto);

        List<SolutionDto> result = solutionService.getAll();
        assertEquals(1, result.size());
        assertEquals(solutionDto, result.get(0));
    }

    @Test
    void getAllByStatus_returnSolutionsWithStatus() {
        when(solutionRepository.findAllByStatusAndIsArchivedFalse(SolutionStatus.SENT)).thenReturn(List.of(solution));
        when(solutionMapper.modelToDto(solution)).thenReturn(solutionDto);

        List<SolutionDto> result = solutionService.getAllByStatus("SENT");

        assertEquals(1, result.size());
        assertEquals(solutionDto, result.get(0));
    }

    @Test
    void getAllByTaskId_returnSolutionsWithTaskId() {
        when(solutionRepository.findAllByTaskIdAndIsArchivedFalse(1L)).thenReturn(List.of(solution));
        when(solutionMapper.modelToDto(solution)).thenReturn(solutionDto);

        List<SolutionDto> result = solutionService.getAllByTaskId(1L);

        assertEquals(1, result.size());
        assertEquals(solutionDto, result.get(0));
    }

    @Test
    void archiveSolutions_archiveSolutionsOfUserWithId() {
        User user = User.builder().id(1L).build();
        List<Solution> solutions = List.of(
                Solution.builder().id(1L).user(user).isArchived(false).build(),
                Solution.builder().id(2L).user(user).isArchived(false).build()
        );

        when(solutionRepository.findAllByUserId(1L)).thenReturn(solutions);

        solutionService.archiveSolutions(1L);

        verify(solutionRepository, times(1)).saveAllAndFlush(solutions);
        solutions.forEach(solution -> assertTrue(solution.getIsArchived()));
    }
}