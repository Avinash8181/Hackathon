package com.tms.serviceimpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dto.TimesheetEntryDTO;
import com.tms.dto.TimesheetHistoryResponse;
import com.tms.dto.TimesheetReviewRequest;
import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.dto.TimesheetSummaryDTO;
import com.tms.entity.Contractor;
import com.tms.entity.Timesheet;
import com.tms.entity.TimesheetEntry;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ActivityRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;
import com.tms.repository.TimesheetEntryRepository;
import com.tms.repository.TimesheetRepository;
import com.tms.service.TimesheetService;

import lombok.extern.slf4j.Slf4j;

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

	    if (timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(
	            request.getContractorId(), request.getWeekStartDate())) {
	        log.warn("Timesheet already exists for contractor ID {} and week {}", 
	                 request.getContractorId(), request.getWeekStartDate());
	        throw new RuntimeException("Timesheet already submitted for this week.");
	    }

	    Contractor contractor = contractorRepo.findById(request.getContractorId()).orElseThrow(() -> {
	        log.error("Contractor not found with ID: {}", request.getContractorId());
	        return new RuntimeException("Contractor not found");
	    });

	    Timesheet timesheet = new Timesheet();
	    timesheet.setContractor(contractor);
	    timesheet.setWeekStartDate(request.getWeekStartDate());
	    timesheet.setWeekEndDate(request.getWeekEndDate());
	    timesheet = timesheetRepo.save(timesheet);
	    log.info("Timesheet created with ID: {}", timesheet.getTimesheetId());

	    if (!request.getEntries().isEmpty()) {
	        TimesheetEntryDTO dto = request.getEntries().get(0);
	        TimesheetEntry entry = new TimesheetEntry();
	        entry.setContractor(contractor);
	        entry.setDate(dto.getDate());
	        entry.setHoursWorked(dto.getHoursWorked().doubleValue());
	        entry.setComments(dto.getManagerComment()); // Assuming managerComment maps to comments

	        entry.setProject(projectRepo.findById(dto.getProjectId()).orElseThrow(() -> {
	            log.error("Project not found with ID: {}", dto.getProjectId());
	            return new RuntimeException("Project not found");
	        }));

	        entry.setActivity(activityRepo.findById(dto.getActivityCode()).orElseThrow(() -> {
	            log.error("Activity not found with code: {}", dto.getActivityCode());
	            return new RuntimeException("Activity not found");
	        }));

	        TimesheetEntry savedEntry = entryRepo.save(entry);
	        log.info("Timesheet entry saved with ID: {}", savedEntry.getEntryId());

	        timesheet.setTimesheetEntry(savedEntry);
	        timesheetRepo.save(timesheet);
	        log.info("Timesheet updated with entry ID: {}", savedEntry.getEntryId());
	    } else {
	        log.warn("No entries found in the timesheet submission request.");
	    }
	}

	@Override
	public String reviewTimesheet(TimesheetReviewRequest request) {
		// Fetch the timesheet using timesheetId
		Timesheet timesheet = timesheetRepo.findById(request.getTimesheetId())
				.orElseThrow(() -> {
					log.error("Timesheet not found for timesheetId: {}", request.getTimesheetId());
					return new ResourceNotFoundException(
							"Timesheet not found for timesheetId: " + request.getTimesheetId());

				});
		// Only allow review if current status is SUBMITTED
		if (!"SUBMITTED".equalsIgnoreCase(timesheet.getStatus())) {
			log.warn("Cannot review timesheetId {} - current status: {}", request.getTimesheetId(),
					timesheet.getStatus());
			throw new IllegalStateException("Only timesheets with status SUBMITTED can be approved or rejected.");

		}
		// Validate decision
		String decision = request.getDecision().toUpperCase();
		if (!decision.equals("APPROVED") && !decision.equals("REJECTED")) {
			log.error("Invalid decision '{}' for timesheetId: {}", decision, request.getTimesheetId());
			throw new IllegalArgumentException("Decision must be APPROVED or REJECTED.");
		}
		// Update status and manager comment
		timesheet.setStatus(decision);
		timesheet.setManagerComment(request.getComments());
		timesheetRepo.save(timesheet);
		log.info("TimesheetId {} reviewed successfully with decision: {}", request.getTimesheetId(), decision);
		return decision;
	}
	@Override
	public TimesheetHistoryResponse getTimesheetHistory(int contractorId, LocalDateTime startDate,
			LocalDateTime endDate) {
		List<Timesheet> timesheets = timesheetRepo.findByContractorContractorId(contractorId);
		if (timesheets.isEmpty()) {
			log.warn("No timesheets found for contractor ID: {}", contractorId);
			throw new ResourceNotFoundException("No timesheets found for contractor ID: " + contractorId);
		}
		log.info("Fetching APPROVED timesheets for contractor ID: {} between {} and {}", contractorId, startDate,
				endDate);
		final double[] totalApprovedHours = { 0.0 };
		ZoneId zoneId = ZoneId.systemDefault();
		List summaries = timesheets.stream().filter(ts -> {
			LocalDateTime weekStart = ts.getWeekStartDate().atZone(zoneId).toLocalDateTime();
			return "APPROVED".equalsIgnoreCase(ts.getStatus())
					&& (weekStart.isEqual(startDate) || weekStart.isAfter(startDate))
					&& (weekStart.isEqual(endDate) || weekStart.isBefore(endDate));
		}).map(ts -> {
			TimesheetEntry entity = ts.getTimesheetEntry();
			TimesheetSummaryDTO dto = new TimesheetSummaryDTO();
			dto.setTimesheetId(ts.getTimesheetId());
			dto.setWeekStart(ts.getWeekStartDate());
			Instant weekStartInstant = ts.getWeekStartDate().atZone(zoneId).toInstant();
			Instant weekEndInstant = weekStartInstant.plus(6, ChronoUnit.DAYS);
			LocalDateTime weekEnd = LocalDateTime.ofInstant(weekEndInstant, zoneId);
			dto.setWeekEnd(weekEnd);
			dto.setStatus(ts.getStatus());
			double hours = entity.getHoursWorked();
			dto.setTotalHours(hours);
			totalApprovedHours[0] += hours;
			return dto;
		}).collect(Collectors.toList());
		return new TimesheetHistoryResponse(summaries, totalApprovedHours[0]);
	}
}
