package com.tms.dto;

import lombok.Data;

@Data
public class TimesheetReviewRequest {
    private int timesheetId;
    private int approvedBy;
    private String decision; // APPROVED or REJECTED
    private String comments;
}
