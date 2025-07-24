package com.tms.service;

import com.tms.dto.ContractorProjectDTO;
import org.springframework.stereotype.Service;


@Service
public interface ContractorProjectService {
    String assignContractorToProject(ContractorProjectDTO dto);
}
