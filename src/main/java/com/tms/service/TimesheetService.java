package com.tms.service;

import com.tms.dto.TimesheetSubmissionRequest;

public interface TimesheetService {
    void submitTimesheet(TimesheetSubmissionRequest request);
}