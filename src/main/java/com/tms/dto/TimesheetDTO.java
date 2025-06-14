package com.tms.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class TimesheetDTO {
    private int contractorId;
    private int projectId;
    private String activityCode;
    private Date date;
    private double hoursWorked;
    private String comments;
    private Date weekStartDate;
    private String status;
    private String managerComment;
}		