package com.tms.serviceImp;

import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.dto.TimesheetSubmissionRequest.Entry;
import com.tms.entity.*;
import com.tms.repository.*;
import com.tms.service.TimesheetService;
import com.tms.serviceImpl.TimesheetServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));

        Entry entry = new Entry();
        entry.setDate(LocalDate.of(2025, 6, 9));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(8.0);
        entry.setComments("Worked on feature X");

        request.setEntries(List.of(entry));

        Contractor contractor = new Contractor();
        Project project = new Project();
        Activity activity = new Activity();
        Timesheet timesheet = new Timesheet();
        TimesheetEntry timesheetEntry = new TimesheetEntry();

        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDate.of(2025, 6, 9))).thenReturn(false);
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
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));

        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDate.of(2025, 6, 9))).thenReturn(true);

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
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));

        Entry entry = new Entry();
        entry.setDate(LocalDate.of(2025, 6, 9));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(8.0);
        entry.setComments("Worked on feature X");

        request.setEntries(List.of(entry));

        when(timesheetRepo.existsByContractor_ContractorIdAndWeekStartDate(1, LocalDate.of(2025, 6, 9))).thenReturn(false);
        when(contractorRepo.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            timesheetService.submitTimesheet(request);
        });

        assertEquals("Contractor not found", exception.getMessage());
    }
}

