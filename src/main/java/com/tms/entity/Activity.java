package com.tms.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Activity {
    @Id
    private String activityCode;

    private String name;
    private String description;

}