package com.tms.controller;

import com.tms.dto.ContractorProjectDTO;
import com.tms.exception.ResourceNotFoundException;
import com.tms.service.ContractorProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContractorProjectControllerTest {

    @InjectMocks
    private ContractorProjectController contractorProjectController;
    @Mock
    private ContractorProjectService contractorProjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignProject_Success() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(1);
        dto.setProjectId(100);
        when(contractorProjectService.assignContractorToProject(dto)).thenReturn("Project assigned successfully");
        ResponseEntity<?> response = contractorProjectController.assignProject(dto);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Project assigned successfully", response.getBody());
    }

    @Test
    void testAssignProject_ResourceNotFound() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(2);
        dto.setProjectId(200);
        when(contractorProjectService.assignContractorToProject(dto))
                .thenThrow(new ResourceNotFoundException("Contractor not found"));
        ResponseEntity<?> response = contractorProjectController.assignProject(dto);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Contractor not found", response.getBody());
    }

    @Test
    void testAssignProject_InternalServerError() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(3);
        dto.setProjectId(300);
        when(contractorProjectService.assignContractorToProject(dto))
                .thenThrow(new RuntimeException("Unexpected DB error"));
        ResponseEntity<?> response = contractorProjectController.assignProject(dto);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody());
    }
}
