package com.tms.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class TimesheetRequest {
    private Integer contractorId;
    private LocalDate weekStartDate;
    private String managerComment;
    private List<TimesheetEntryDTO> entries;
}
