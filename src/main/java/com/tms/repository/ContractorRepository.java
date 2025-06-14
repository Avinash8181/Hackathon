package com.tms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.entity.Contractor;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Integer> {
    // Add custom query methods if needed
}
