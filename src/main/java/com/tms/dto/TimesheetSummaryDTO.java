package com.tms.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class TimesheetSummaryDTO {
    private int timesheetId;
    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;
    private String status;
    private double totalHours;
}