package com.tms.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TimesheetSubmissionRequest {
    private Integer contractorId;
    private LocalDateTime weekStartDate;
    private LocalDateTime weekEndDate; 
    private String managerComment;
    private List<TimesheetEntryDTO> entries;
}