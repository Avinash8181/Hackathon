package com.tms.controller;

import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.dto.TimesheetSubmissionRequest.Entry;
import com.tms.service.TimesheetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TimesheetControllerTest {

    @Mock
    private TimesheetService timesheetService;


    @InjectMocks
    private TimesheetController timesheetController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Positive Test Case
    @Test
    void testSubmitTimesheet_Success() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(101);
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));

        Entry entry = new Entry();
        entry.setDate(LocalDate.of(2025, 6, 9));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(8.0);
        entry.setComments("Worked on feature X");

        request.setEntries(List.of(entry));

        doNothing().when(timesheetService).submitTimesheet(request);

        String response = timesheetController.submitTimesheet(request);

        assertEquals("Timesheet submitted successfully!", response);
        verify(timesheetService, times(1)).submitTimesheet(request);
    }

    // ❌ Negative Test Case: Service throws exception
    @Test
    void testSubmitTimesheet_ServiceThrowsException() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(101);
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));

        Entry entry = new Entry();
        entry.setDate(LocalDate.of(2025, 6, 9));
        entry.setProjectId(1001);
        entry.setActivityCode("DEV");
        entry.setHoursWorked(8.0);
        entry.setComments("Worked on feature X");

        request.setEntries(List.of(entry));

        doThrow(new RuntimeException("Database error")).when(timesheetService).submitTimesheet(request);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            timesheetController.submitTimesheet(request);
        });

        assertEquals("Database error", exception.getMessage());
        verify(timesheetService, times(1)).submitTimesheet(request);
    }

    // ❌ Negative Test Case: Empty entries list
    @Test
    void testSubmitTimesheet_EmptyEntries() {
        TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
        request.setContractorId(101);
        request.setWeekStartDate(LocalDate.of(2025, 6, 9));
        request.setEntries(Collections.emptyList());

        doNothing().when(timesheetService).submitTimesheet(request);

        String response = timesheetController.submitTimesheet(request);

        assertEquals("Timesheet submitted successfully!", response);
        verify(timesheetService, times(1)).submitTimesheet(request);
    }
}
