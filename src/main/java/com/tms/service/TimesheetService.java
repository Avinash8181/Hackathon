package com.tms.service;

import java.time.LocalDateTime;
import com.tms.dto.TimesheetHistoryResponse;
import com.tms.dto.TimesheetReviewRequest;
import com.tms.dto.TimesheetSubmissionRequest;

public interface TimesheetService {
    void submitTimesheet(TimesheetSubmissionRequest request);
    String reviewTimesheet(TimesheetReviewRequest request);
    TimesheetHistoryResponse getTimesheetHistory(int contractorId, LocalDateTime startDate, LocalDateTime endDate);   
}