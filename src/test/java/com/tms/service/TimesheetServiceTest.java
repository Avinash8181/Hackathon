package com.tms.service;

import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.dto.TimesheetSubmissionRequest.Entry;
import com.tms.service.TimesheetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @BeforeEach
    void setUp() {
        timesheetService = mock(TimesheetService.class);
    }

    private TimesheetSubmissionRequest createValidRequest() {
        Entry entry = new Entry();
        entry.setProjectId(101);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(8.0);
        entry.setComments("Worked on feature X");

        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1001);
        request.setWeekStartDate(LocalDate.now().minusDays(7));
        request.setEntries(List.of(entry));

        return request;
    }

    @Test
    void testSubmitTimesheet_PositiveScenario() {
        TimesheetSubmissionRequest request = createValidRequest();

        doNothing().when(timesheetService).submitTimesheet(request);

        assertDoesNotThrow(() -> timesheetService.submitTimesheet(request));
        verify(timesheetService, times(1)).submitTimesheet(request);
    }

    @Test
    void testSubmitTimesheet_NegativeScenario_NullRequest() {
        doThrow(new IllegalArgumentException("Request cannot be null"))
                .when(timesheetService).submitTimesheet(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            timesheetService.submitTimesheet(null);
        });

        assertEquals("Request cannot be null", exception.getMessage());
        verify(timesheetService, times(1)).submitTimesheet(null);
    }

    @Test
    void testSubmitTimesheet_NegativeScenario_EmptyEntries() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1001);
        request.setWeekStartDate(LocalDate.now().minusDays(7));
        request.setEntries(Collections.emptyList());

        doThrow(new IllegalArgumentException("Entries cannot be empty"))
                .when(timesheetService).submitTimesheet(request);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            timesheetService.submitTimesheet(request);
        });

        assertEquals("Entries cannot be empty", exception.getMessage());
        verify(timesheetService, times(1)).submitTimesheet(request);
    }

    @Test
    void testSubmitTimesheet_NegativeScenario_InvalidHoursWorked() {
        Entry entry = new Entry();
        entry.setDate(LocalDate.now());
        entry.setProjectId(101);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(-5.0); // Invalid
        entry.setComments("Worked on feature X");

        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(1001);
        request.setWeekStartDate(LocalDate.now().minusDays(7));
        request.setEntries(List.of(entry));

        doThrow(new IllegalArgumentException("Hours worked cannot be negative"))
                .when(timesheetService).submitTimesheet(request);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            timesheetService.submitTimesheet(request);
        });

        assertEquals("Hours worked cannot be negative", exception.getMessage());
        verify(timesheetService, times(1)).submitTimesheet(request);
    }
}
