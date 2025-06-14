package com.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.TimesheetEntry;
 
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Integer> {}
