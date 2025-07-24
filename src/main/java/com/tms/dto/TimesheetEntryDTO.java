package com.tms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TimesheetEntryDTO {
    private Integer projectId;
    private String activityCode;
    private LocalDateTime date;
    private BigDecimal hoursWorked;
    private String status;
    private String managerComment;
}
