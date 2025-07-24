package com.tms.controller;


import com.tms.dto.LogHoursDTO;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.service.LogHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogHoursControllerTest {

    @InjectMocks
    private LogHoursController logHoursController;
    @Mock
    private LogHoursService logHoursService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    private LogHoursDTO createSampleDTO() {
        LogHoursDTO dto = new LogHoursDTO();
        dto.setContractorId(1);
        dto.setProjectCode("PRJ001");
        dto.setActivityType("Development");
        dto.setDate(LocalDateTime.now());
        dto.setHoursWorked(8.0);
        dto.setStatus("SUBMITTED");
        return dto;
    }
    @Test
    void testLogHours_Success() {
        LogHoursDTO dto = createSampleDTO();
        when(logHoursService.logHours(dto)).thenReturn("Hours logged successfully");
        ResponseEntity<?> response = logHoursController.logHours(dto);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Hours logged successfully", response.getBody());
    }
    @Test
    void testLogHours_BadRequestException() {
        LogHoursDTO dto = createSampleDTO();
        when(logHoursService.logHours(dto)).thenThrow(new BadRequestException("Invalid hours"));
        ResponseEntity<?> response = logHoursController.logHours(dto);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid hours", response.getBody());
    }

	@Test
	void testLogHours_ResourceNotFoundException() {
		LogHoursDTO dto = createSampleDTO();
		when(logHoursService.logHours(dto)).thenThrow(new ResourceNotFoundException("Contractor not found"));
	
		ResponseEntity<?> response = logHoursController.logHours(dto);
	
		assertEquals(404, response.getStatusCode().value()); // Updated line
		assertEquals("Contractor not found", response.getBody());
	}

    @Test
    void testLogHours_InternalServerError() {
        LogHoursDTO dto = createSampleDTO();
        when(logHoursService.logHours(dto)).thenThrow(new RuntimeException("Unexpected error"));
        ResponseEntity<?> response = logHoursController.logHours(dto);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody());
    }
}
