package com.tms.controller;

import com.tms.dto.ContractorProjectDTO;
import com.tms.service.ContractorProjectService;
import com.tms.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class ContractorProjectController {

    private final ContractorProjectService contractorProjectService;

    // ✅ Constructor injection instead of field injection
    public ContractorProjectController(ContractorProjectService contractorProjectService) {
        this.contractorProjectService = contractorProjectService;
    }

    @PostMapping("/assign-project")
    public ResponseEntity<String> assignProject(@RequestBody ContractorProjectDTO dto) {
        try {
            String message = contractorProjectService.assignContractorToProject(dto);
            return ResponseEntity.ok(message);
        } catch (ResourceNotFoundException ex) {
            log.error("Error assigning project: {}", ex.getMessage());
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
