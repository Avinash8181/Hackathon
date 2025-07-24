package com.tms.repository;

import com.tms.entity.ContractorProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorProjectRepository extends JpaRepository<ContractorProject, Integer> {
}
