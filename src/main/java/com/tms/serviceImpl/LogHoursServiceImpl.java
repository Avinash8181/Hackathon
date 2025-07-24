package com.tms.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dto.LogHoursDTO;
import com.tms.entity.Activity;
import com.tms.entity.Contractor;
import com.tms.entity.Project;
import com.tms.entity.TimesheetEntry;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ActivityRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;
import com.tms.repository.TimesheetEntryRepository;
import com.tms.service.LogHoursService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogHoursServiceImpl implements LogHoursService {

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    @Override
    public String logHours(LogHoursDTO dto) {
        log.info("Logging hours for contractor: {}", dto.getContractorId());

        if (dto.getHoursWorked() < 0 || dto.getHoursWorked() > 24) {
            throw new BadRequestException("Hours worked must be between 0 and 24.");
        }

        Contractor contractor = contractorRepository.findById(dto.getContractorId())
                .orElseThrow(() -> new ResourceNotFoundException("Contractor not found."));

        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        if (project == null) {
            throw new ResourceNotFoundException("Project not found.");
        }

        Activity activity = activityRepository.findById(dto.getActivityType())
                .orElseThrow(() -> new ResourceNotFoundException("Activity type not found."));

        TimesheetEntry entry = new TimesheetEntry();
        entry.setContractor(contractor);
        entry.setProject(project);
        entry.setActivity(activity);
        entry.setDate(dto.getDate());
        entry.setHoursWorked(dto.getHoursWorked());
        entry.setComments(dto.getStatus());

        timesheetEntryRepository.save(entry);
        log.info("Timesheet entry saved successfully.");

        return "Timesheet entry saved as draft.";
    }
}
