package com.tms.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogHoursDTO {
    private int contractorId;
    private String projectCode;
    private String activityType;
    private LocalDateTime date;
    private double hoursWorked;
    private String status; // DRAFT or SUBMITTED
}

