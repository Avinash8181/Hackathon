package com.tms.serviceImpl;

import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.entity.Contractor;
import com.tms.entity.Timesheet;
import com.tms.entity.TimesheetEntry;
import com.tms.repository.*;
import com.tms.service.TimesheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepo;

    @Autowired
    private TimesheetEntryRepository entryRepo;

    @Autowired
    private ContractorRepository contractorRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private ActivityRepository activityRepo;

    @Override
    public void submitTimesheet(TimesheetSubmissionRequest request) {
        log.info("Submitting timesheet for contractor ID: {}", request.getContractorId());

        if (timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(request.getContractorId(), request.getWeekStartDate())) {
            log.warn("Timesheet already exists for contractor ID {} and week {}", request.getContractorId(), request.getWeekStartDate());
            throw new RuntimeException("Timesheet already submitted for this week.");
        }

        Contractor contractor = contractorRepo.findById(request.getContractorId())
                .orElseThrow(() -> {
                    log.error("Contractor not found with ID: {}", request.getContractorId());
                    return new RuntimeException("Contractor not found");
                });

        Timesheet timesheet = new Timesheet();
        timesheet.setContractor(contractor);
        timesheet.setWeekStartDate(request.getWeekStartDate());
        timesheet.setStatus("SUBMITTED");

        timesheet = timesheetRepo.save(timesheet);
        log.info("Timesheet created with ID: {}", timesheet.getTimesheetId());

        if (!request.getEntries().isEmpty()) {
            TimesheetSubmissionRequest.Entry e = request.getEntries().get(0);
            TimesheetEntry entry = new TimesheetEntry();
            entry.setContractor(contractor);
            entry.setDate(e.getDate());
            entry.setProject(projectRepo.findById(e.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found with ID: {}", e.getProjectId());
                        return new RuntimeException("Project not found");
                    }));
            entry.setActivity(activityRepo.findById(e.getActivityCode())
                    .orElseThrow(() -> {
                        log.error("Activity not found with code: {}", e.getActivityCode());
                        return new RuntimeException("Activity not found");
                    }));
            entry.setHoursWorked(e.getHoursWorked());
            entry.setComments(e.getComments());

            TimesheetEntry savedEntry = entryRepo.save(entry);
            log.info("Timesheet entry saved with ID: {}", savedEntry.getEntryId());

            timesheet.setTimesheetEntry(savedEntry);
            timesheetRepo.save(timesheet);
            log.info("Timesheet updated with entry ID: {}", savedEntry.getEntryId());
        } else {
            log.warn("No entries found in the timesheet submission request.");
        }
    }
}
