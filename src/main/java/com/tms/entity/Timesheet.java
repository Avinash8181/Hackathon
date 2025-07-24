package com.tms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;



@Data
@Entity
public class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int timesheetId;

    @OneToOne
    @JoinColumn(name="entryId")
    private  TimesheetEntry timesheetEntry;
    private LocalDateTime weekStartDate;
    private LocalDateTime weekEndDate;

    @ManyToOne
    @JoinColumn(name = "contractorId")
    private Contractor contractor;

    private String status; // DRAFT, SUBMITTED, APPROVED, REJECTED
    private String managerComment;

}
