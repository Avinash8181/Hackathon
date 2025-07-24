package com.tms.controller;

import com.tms.dto.LogHoursDTO;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.service.LogHoursService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/contractor")
public class LogHoursController {

    @Autowired
    private LogHoursService logHoursService;

    @PostMapping("/log-hours")
    public ResponseEntity<?> logHours(@RequestBody LogHoursDTO dto) {
        try {
            String message = logHoursService.logHours(dto);
            return ResponseEntity.ok().body(message);
        } catch (BadRequestException | ResourceNotFoundException ex) {
            log.error("Error logging hours: {}", ex.getMessage());
            return ResponseEntity.status(ex instanceof BadRequestException ? 400 : 404).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
