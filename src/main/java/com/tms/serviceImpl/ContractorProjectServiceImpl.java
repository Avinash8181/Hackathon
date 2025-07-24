package com.tms.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dto.ContractorProjectDTO;
import com.tms.entity.Contractor;
import com.tms.entity.ContractorProject;
import com.tms.entity.Project;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ContractorProjectRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;
import com.tms.service.ContractorProjectService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractorProjectServiceImpl implements ContractorProjectService {

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContractorProjectRepository contractorProjectRepository;

    @Override
    public String assignContractorToProject(ContractorProjectDTO dto) {
        log.info("Assigning contractor {} to project {}", dto.getContractorId(), dto.getProjectId());

        Contractor contractor = contractorRepository.findById(dto.getContractorId())
                .orElseThrow(() -> new ResourceNotFoundException("Contractor not found"));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ContractorProject assignment = new ContractorProject();
        assignment.setContractor(contractor);
        assignment.setProject(project);

        contractorProjectRepository.save(assignment);
        log.info("Assignment saved successfully");

        return "Contractor assigned to project successfully.";
    }
}
