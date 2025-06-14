package com.tms.entity;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class ContractorProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignmentId;

    @ManyToOne
    @JoinColumn(name = "contractorId")
    private Contractor contractor;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

}