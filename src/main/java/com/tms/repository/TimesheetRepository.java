package com.tms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.Timesheet;
 
public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> {
    boolean existsByContractor_ContractorIdAndWeekStartDate(Integer contractorId, LocalDateTime date);
    List<Timesheet> findByContractorContractorId(int contractorId);
    Optional<Timesheet> findByTimesheetId(int entryId);
    List<Timesheet> findByStatus(String status);
}