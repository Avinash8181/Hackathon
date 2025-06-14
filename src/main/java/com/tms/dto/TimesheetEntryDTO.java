package com.tms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TimesheetEntryDTO {
    private Integer projectId;
    private Integer activityCode;
    private LocalDate date;
    private BigDecimal hoursWorked;
    private String comments;

}
