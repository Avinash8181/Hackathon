package com.tms.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tms.dto.TimesheetEntryDTO;
import com.tms.dto.TimesheetHistoryResponse;
import com.tms.dto.TimesheetReviewRequest;
import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.dto.TimesheetSummaryDTO;
import com.tms.entity.Activity;
import com.tms.entity.Contractor;
import com.tms.entity.Project;
import com.tms.entity.Timesheet;
import com.tms.entity.TimesheetEntry;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ActivityRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;
import com.tms.repository.TimesheetEntryRepository;
import com.tms.repository.TimesheetRepository;

public class TimesheetServiceImplTest {

    @Mock
    private TimesheetRepository timesheetRepo;
    @Mock
    private TimesheetEntryRepository entryRepo;
    @Mock
    private ContractorRepository contractorRepo;
    @Mock
    private ProjectRepository projectRepo;
    @Mock
    private ActivityRepository activityRepo;
    @InjectMocks
    private TimesheetServiceImpl timesheetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Positive Test Case
    @Test
    void testSubmitTimesheet_Success() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1);
        request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
        TimesheetEntryDTO entry = new TimesheetEntryDTO();
        entry.setDate(LocalDateTime.of(2025, 6, 9, 0, 0));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(BigDecimal.valueOf(5.5)); 
        entry.setManagerComment("Good");
        request.setEntries(List.of(entry));
        Contractor contractor = new Contractor();
        Project project = new Project();
        Activity activity = new Activity();
        Timesheet timesheet = new Timesheet();
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDateTime.of(2025, 6, 9, 0, 0))).thenReturn(false);
        when(contractorRepo.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepo.findById(1001)).thenReturn(Optional.of(project));
        when(activityRepo.findById("DEV")).thenReturn(Optional.of(activity));
        when(timesheetRepo.save(any(Timesheet.class))).thenReturn(timesheet);
        when(entryRepo.save(any(TimesheetEntry.class))).thenReturn(timesheetEntry);
        assertDoesNotThrow(() -> timesheetService.submitTimesheet(request));
        verify(timesheetRepo, times(2)).save(any(Timesheet.class));
        verify(entryRepo).save(any(TimesheetEntry.class));
    }

    // ❌ Negative Test Case: Timesheet already exists
    @Test
    void testSubmitTimesheet_AlreadyExists() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1);
        request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDateTime.of(2025, 6, 9, 0, 0))).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            timesheetService.submitTimesheet(request);
        });
        assertEquals("Timesheet already submitted for this week.", exception.getMessage());
    }

    // ❌ Negative Test Case: Contractor not found
    @Test
    void testSubmitTimesheet_ContractorNotFound() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1);
        request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
        TimesheetEntryDTO entry = new TimesheetEntryDTO();
        entry.setDate(LocalDateTime.of(2025, 6, 9, 0, 0));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(BigDecimal.valueOf(5.5)); 
        entry.setManagerComment("Good");
        request.setEntries(List.of(entry));
        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDateTime.of(2025, 6, 9, 0, 0))).thenReturn(false);
        when(contractorRepo.findById(1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            timesheetService.submitTimesheet(request);
        });
        assertEquals("Contractor not found", exception.getMessage());
    }
    @Test
    void testReviewTimesheet_ApprovedSuccessfully() {
        TimesheetReviewRequest request = new TimesheetReviewRequest();
        request.setTimesheetId(1);
        request.setDecision("APPROVED");
        request.setComments("Looks good");
        Timesheet timesheet = new Timesheet();
        timesheet.setTimesheetId(1);
        timesheet.setStatus("SUBMITTED");
        when(timesheetRepo.findById(1)).thenReturn(Optional.of(timesheet));
        String result = timesheetService.reviewTimesheet(request);
        assertEquals("APPROVED", result);
        verify(timesheetRepo).save(timesheet);
    }

    @Test
    void testReviewTimesheet_TimesheetNotFound() {
        TimesheetReviewRequest request = new TimesheetReviewRequest();
        request.setTimesheetId(99);
        request.setDecision("APPROVED");
        when(timesheetRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> timesheetService.reviewTimesheet(request));
    }

    @Test
    void testReviewTimesheet_InvalidStatus() {
        TimesheetReviewRequest request = new TimesheetReviewRequest();
        request.setTimesheetId(1);
        request.setDecision("APPROVED");
        Timesheet timesheet = new Timesheet();
        timesheet.setTimesheetId(1);
        timesheet.setStatus("DRAFT");
        when(timesheetRepo.findById(1)).thenReturn(Optional.of(timesheet));
        assertThrows(IllegalStateException.class, () -> timesheetService.reviewTimesheet(request));
    }

    @Test
    void testReviewTimesheet_InvalidDecision() {
        TimesheetReviewRequest request = new TimesheetReviewRequest();
        request.setTimesheetId(1);
        request.setDecision("INVALID");
        request.setComments("N/A");
        Timesheet timesheet = new Timesheet();
        timesheet.setTimesheetId(1);
        timesheet.setStatus("SUBMITTED");
        when(timesheetRepo.findById(1)).thenReturn(Optional.of(timesheet));
        assertThrows(IllegalArgumentException.class, () -> timesheetService.reviewTimesheet(request));
    }

    
    @Test
    void testGetTimesheetHistory_NoTimesheetsFound() {
        when(timesheetRepo.findByContractorContractorId(999)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () ->
                timesheetService.getTimesheetHistory(999, LocalDateTime.now().minusDays(5), LocalDateTime.now()));
    }
    @Test
    public void testGetTimesheetHistory_NoTimesheets_ThrowsException() {
        int contractorId = 101;
        when(timesheetRepo.findByContractorContractorId(contractorId)).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                timesheetService.getTimesheetHistory(contractorId,
                        LocalDateTime.of(2025, 6, 1, 0, 0),
                        LocalDateTime.of(2025, 6, 30, 23, 59)));

        assertEquals("No timesheets found for contractor ID: 101", exception.getMessage());
    }

    @Test
    public void testGetTimesheetHistory_ApprovedTimesheetsWithinRange_ReturnsSummary() {
        int contractorId = 101;
        LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 23, 59);

        TimesheetEntry entry = new TimesheetEntry();
        entry.setHoursWorked(8.0);

        Timesheet timesheet = new Timesheet();
        timesheet.setTimesheetId(1);
        timesheet.setStatus("APPROVED");
        timesheet.setWeekStartDate(LocalDateTime.of(2025, 6, 10, 0, 0));
        timesheet.setTimesheetEntry(entry);

        when(timesheetRepo.findByContractorContractorId(contractorId)).thenReturn(List.of(timesheet));

        TimesheetHistoryResponse response = timesheetService.getTimesheetHistory(contractorId, startDate, endDate);

        assertNotNull(response);
        assertEquals(1, response.getTimesheetSummaries().size());
        assertEquals(8.0, response.getTotalApprovedHours());
        TimesheetSummaryDTO summary = response.getTimesheetSummaries().get(0);
        assertEquals(1, summary.getTimesheetId());
        assertEquals("APPROVED", summary.getStatus());
        assertEquals(8.0, summary.getTotalHours());
    }
}

