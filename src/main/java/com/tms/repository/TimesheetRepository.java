package com.tms.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.Timesheet;

 
public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> {
    boolean existsByContractor_ContractorIdAndWeekStartDate(Integer contractorId, LocalDate date);
}