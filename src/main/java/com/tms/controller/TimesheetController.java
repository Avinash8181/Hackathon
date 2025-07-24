package com.tms.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tms.dto.TimesheetHistoryResponse;
import com.tms.dto.TimesheetReviewRequest;
import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.service.TimesheetService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/timesheets/")
public class TimesheetController {
 
    @Autowired
    private TimesheetService timesheetService;
 
    @PutMapping
    public String submitTimesheet(@RequestBody TimesheetSubmissionRequest request) {
        timesheetService.submitTimesheet(request);
        return "Timesheet submitted successfully!";
    }
    
    @PostMapping("/manager/review-timesheet")
    public ResponseEntity<Map<String, Object>> reviewTimesheet(@RequestBody TimesheetReviewRequest request) {
        String decision = timesheetService.reviewTimesheet(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Timesheet reviewed and updated.");
        response.put("statusCode", 200);
        response.put("decision", decision);
        log.info("Review successful for timesheet ID: {}", request.getTimesheetId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contractor/timesheet-history")
    public ResponseEntity<Map<String, Object>> getTimesheetHistory(
            @RequestParam int contractorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Received request for timesheet history for contractor ID: {}", contractorId);
        TimesheetHistoryResponse timesheets = timesheetService.getTimesheetHistory(contractorId, startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Timesheet history retrieved successfully.");
        response.put("statusCode", 200);
        response.put("contractorId", contractorId);
        response.put("timesheets", timesheets);
        log.info("Timesheet history response prepared for contractor ID: {}", contractorId);
        return ResponseEntity.ok(response);
    }
}

