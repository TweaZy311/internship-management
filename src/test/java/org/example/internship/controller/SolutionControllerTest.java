package org.example.internship.controller;

import org.example.internship.dto.request.solution.SolutionStatusDto;
import org.example.internship.dto.response.solution.SolutionDto;
import org.example.internship.service.gitlab.GitlabService;
import org.example.internship.service.solution.SolutionService;
import org.gitlab4j.api.systemhooks.PushSystemHookEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionControllerTest {

    @Mock
    private SolutionService solutionService;

    @Mock
    private GitlabService gitlabService;

    @InjectMocks
    private SolutionController solutionController;

    @Test
    void addSolution_returnCreated() {
        PushSystemHookEvent request = mock(PushSystemHookEvent.class);
        when(gitlabService.isForkedRepository(anyLong())).thenReturn(true);

        ResponseEntity<Void> response = solutionController.addSolution(request);

        verify(solutionService, times(1)).add(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateSolutionStatus_returnOk() {
        SolutionStatusDto dto = new SolutionStatusDto();
        dto.setId(1L);
        dto.setStatus("approved");

        ResponseEntity<Void> response = solutionController.updateSolutionStatus(dto);

        verify(solutionService, times(1)).updateStatus(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSolutionById_returnSolution() {
        SolutionDto solutionDto = new SolutionDto();
        solutionDto.setId(1L);

        when(solutionService.getById(1L)).thenReturn(solutionDto);

        ResponseEntity<SolutionDto> response = solutionController.getSolutionById(1L);

        verify(solutionService, times(1)).getById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(solutionDto, response.getBody());
    }

    @Test
    void getAllSolutions_withStatus_returnListOfSolutions() {
        List<SolutionDto> solutionList = List.of(new SolutionDto());
        when(solutionService.getAllByStatus("approved")).thenReturn(solutionList);

        ResponseEntity<List<SolutionDto>> response = solutionController.getAllSolutions("approved", null);

        verify(solutionService, times(1)).getAllByStatus("approved");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(solutionList, response.getBody());
    }

    @Test
    void getAllSolutions_withTaskId_returnListOfSolutions() {
        List<SolutionDto> solutions = List.of(new SolutionDto());

        when(solutionService.getAllByTaskId(1L)).thenReturn(solutions);

        ResponseEntity<List<SolutionDto>> response = solutionController.getAllSolutions(null, 1L);

        verify(solutionService, times(1)).getAllByTaskId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(solutions, response.getBody());
    }

    @Test
    void getAllSolutions_returnListOfSolutions() {
        List<SolutionDto> solutions = List.of(new SolutionDto());

        when(solutionService.getAll()).thenReturn(solutions);

        ResponseEntity<List<SolutionDto>> response = solutionController.getAllSolutions(null, null);

        verify(solutionService, times(1)).getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(solutions, response.getBody());
    }

    @Test
    void getAllSolutions_withBothParams_returnBadRequest() {
        ResponseEntity<List<SolutionDto>> response = solutionController.getAllSolutions("approved", 1L);

        verify(solutionService, never()).getAllByStatus("approved");
        verify(solutionService, never()).getAllByTaskId(1L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllSolutions_emptyList_returnNoContent() {
        when(solutionService.getAll()).thenReturn(List.of());

        ResponseEntity<List<SolutionDto>> response = solutionController.getAllSolutions(null, null);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}