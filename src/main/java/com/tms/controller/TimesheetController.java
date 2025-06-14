package com.tms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.service.TimesheetService;

@RestController
@RequestMapping("/api/submit")
public class TimesheetController {
 
    @Autowired
    private TimesheetService timesheetService;
 
    @PutMapping
    public String submitTimesheet(@RequestBody TimesheetSubmissionRequest request) {
        timesheetService.submitTimesheet(request);
        return "Timesheet submitted successfully!";
    }
}

