package com.tms.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tms.dto.ContractorProjectDTO;
import com.tms.entity.Contractor;
import com.tms.entity.ContractorProject;
import com.tms.entity.Project;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ContractorProjectRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;

class ContractorProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ContractorProjectRepository contractorProjectRepository;
    @InjectMocks
    private ContractorProjectServiceImpl contractorProjectService;
    @Mock
    private ContractorRepository contractorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignContractorToProject_Success() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(1);
        dto.setProjectId(2);

        Contractor contractor = new Contractor();
        Project project = new Project();

        when(contractorRepository.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepository.findById(2)).thenReturn(Optional.of(project));

        String result = contractorProjectService.assignContractorToProject(dto);

        verify(contractorProjectRepository, times(1)).save(any(ContractorProject.class));
        assertEquals("Contractor assigned to project successfully.", result);
    }

    @Test
    void testAssignContractorToProject_ContractorNotFound() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(1);
        dto.setProjectId(2);

        when(contractorRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                contractorProjectService.assignContractorToProject(dto));

        assertEquals("Contractor not found", exception.getMessage());
    }

    @Test
    void testAssignContractorToProject_ProjectNotFound() {
        ContractorProjectDTO dto = new ContractorProjectDTO();
        dto.setContractorId(1);
        dto.setProjectId(2);

        Contractor contractor = new Contractor();

        when(contractorRepository.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepository.findById(2)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                contractorProjectService.assignContractorToProject(dto));

        assertEquals("Project not found", exception.getMessage());
    }
}
