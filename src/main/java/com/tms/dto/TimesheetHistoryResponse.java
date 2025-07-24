package com.tms.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimesheetHistoryResponse {
    private List<TimesheetSummaryDTO> timesheetSummaries;
    private double totalApprovedHours;
 
    public TimesheetHistoryResponse(List<TimesheetSummaryDTO> timesheetSummaries, double totalApprovedHours) {
        this.timesheetSummaries = timesheetSummaries;
        this.totalApprovedHours = totalApprovedHours;
    }
 
    
}
