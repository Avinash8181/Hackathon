package com.tms.controller;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tms.dto.TimesheetEntryDTO;
import com.tms.dto.TimesheetHistoryResponse;
import com.tms.dto.TimesheetReviewRequest;
import com.tms.dto.TimesheetSubmissionRequest;
import com.tms.service.TimesheetService;

public class TimesheetControllerTest {

	@Mock
	private TimesheetService timesheetService;
	@InjectMocks
	private TimesheetController timesheetController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// ✅ Positive Test Case
	@Test
	void testSubmitTimesheet_Success() {
		TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
		request.setContractorId(101);
		request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
		TimesheetEntryDTO entry = new TimesheetEntryDTO();
		entry.setDate(LocalDateTime.of(2025, 6, 9, 0, 0));
		entry.setProjectId(1001);
		entry.setActivityCode("DEV101");
		entry.setHoursWorked(BigDecimal.valueOf(5.5));
		entry.setManagerComment("Good");
		request.setEntries(List.of(entry));
		doNothing().when(timesheetService).submitTimesheet(request);
		String response = timesheetController.submitTimesheet(request);
		assertEquals("Timesheet submitted successfully!", response);
		verify(timesheetService, times(1)).submitTimesheet(request);
	}

	// ❌ Submit Timesheet - Service throws exception
	@Test
	void testSubmitTimesheet_ServiceThrowsException() {
		TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
		request.setContractorId(101);
		request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
		TimesheetEntryDTO entry = new TimesheetEntryDTO();
		entry.setDate(LocalDateTime.of(2025, 6, 9, 0, 0));
		entry.setProjectId(1001);
		entry.setActivityCode("DEV");
		entry.setHoursWorked(BigDecimal.valueOf(5.5));
		entry.setManagerComment("Good");
		request.setEntries(List.of(entry));
		doThrow(new RuntimeException("Database error")).when(timesheetService).submitTimesheet(request);
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			timesheetController.submitTimesheet(request);
		});
		assertEquals("Database error", exception.getMessage());
		verify(timesheetService, times(1)).submitTimesheet(request);
	}

	// ❌ Submit Timesheet - Empty entries
	@Test
	void testSubmitTimesheet_EmptyEntries() {
		TimesheetSubmissionRequest request = new TimesheetSubmissionRequest();
		request.setContractorId(101);
		request.setWeekStartDate(LocalDateTime.of(2025, 6, 9, 0, 0));
		request.setEntries(Collections.emptyList());
		doNothing().when(timesheetService).submitTimesheet(request);
		String response = timesheetController.submitTimesheet(request);
		assertEquals("Timesheet submitted successfully!", response);
		verify(timesheetService, times(1)).submitTimesheet(request);
	}

	// ✅ Review Timesheet - Approved
	@Test
	void testReviewTimesheet_Success() {
		TimesheetReviewRequest request = new TimesheetReviewRequest();
		request.setTimesheetId(1);
		request.setApprovedBy(1001);
		request.setDecision("APPROVED");
		request.setComments("Reviewed and approved");
		when(timesheetService.reviewTimesheet(request)).thenReturn("APPROVED");
		ResponseEntity<Map<String, Object>> response = timesheetController.reviewTimesheet(request);
		assertEquals(200, response.getStatusCode().value());
		assertEquals("Timesheet reviewed and updated.", response.getBody().get("message"));
		assertEquals("APPROVED", response.getBody().get("decision"));
	}

	// ✅ Review Timesheet - Rejected
	@Test
	void testReviewTimesheet_ResponseStructure() {
		TimesheetReviewRequest request = new TimesheetReviewRequest();
		request.setTimesheetId(2);
		request.setApprovedBy(1002);
		request.setDecision("REJECTED");
		request.setComments("Missing entries");
		when(timesheetService.reviewTimesheet(request)).thenReturn("REJECTED");
		ResponseEntity<Map<String, Object>> response = timesheetController.reviewTimesheet(request);
		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals(200, body.get("statusCode"));
		assertEquals("REJECTED", body.get("decision"));
	}

	// ✅ Get Timesheet History - Success
	@Test
	void testGetTimesheetHistory_Success() {
		int contractorId = 101;
		LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 0, 0);
		TimesheetHistoryResponse mockResponse = new TimesheetHistoryResponse();
		when(timesheetService.getTimesheetHistory(contractorId, startDate, endDate)).thenReturn(mockResponse);
		ResponseEntity<Map<String, Object>> responseEntity = timesheetController.getTimesheetHistory(contractorId,
				startDate, endDate);
		assertEquals(200, responseEntity.getStatusCode().value());
		Map<String, Object> responseBody = responseEntity.getBody();
		assertNotNull(responseBody);
		assertEquals("Timesheet history retrieved successfully.", responseBody.get("message"));
		assertEquals(200, responseBody.get("statusCode"));
		assertEquals(contractorId, responseBody.get("contractorId"));
		assertEquals(mockResponse, responseBody.get("timesheets"));
		verify(timesheetService, times(1)).getTimesheetHistory(contractorId, startDate, endDate);
	}

	// ❌ Get Timesheet History - Service returns null
	@Test
	void testGetTimesheetHistory_ServiceReturnsNull() {
		int contractorId = 102;
		LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 0, 0);
		when(timesheetService.getTimesheetHistory(contractorId, startDate, endDate)).thenReturn(null);
		ResponseEntity<Map<String, Object>> responseEntity = timesheetController.getTimesheetHistory(contractorId,
				startDate, endDate);
		assertEquals(200, responseEntity.getStatusCode().value());
		Map<String, Object> responseBody = responseEntity.getBody();
		assertNotNull(responseBody);
		assertEquals("Timesheet history retrieved successfully.", responseBody.get("message"));
		assertEquals(200, responseBody.get("statusCode"));
		assertEquals(contractorId, responseBody.get("contractorId"));
		assertNull(responseBody.get("timesheets"));
		verify(timesheetService, times(1)).getTimesheetHistory(contractorId, startDate, endDate);
	}
}
