package com.tms.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.tms.dto.LogHoursDTO;
import com.tms.entity.Activity;
import com.tms.entity.Contractor;
import com.tms.entity.Project;
import com.tms.entity.TimesheetEntry;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.ActivityRepository;
import com.tms.repository.ContractorRepository;
import com.tms.repository.ProjectRepository;
import com.tms.repository.TimesheetEntryRepository;

public class LogHoursServiceImplTest {

    @Mock
    private ContractorRepository contractorRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TimesheetEntryRepository timesheetEntryRepository;

    @InjectMocks
    private LogHoursServiceImpl logHoursService;

    private LogHoursDTO dto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dto = new LogHoursDTO();
        dto.setContractorId(1);
        dto.setProjectCode("PRJ001");
        dto.setActivityType("ACT100");
        dto.setDate(LocalDateTime.now());
        dto.setHoursWorked(8);
        dto.setStatus("DRAFT");
    }

    @Test
    public void testLogHours_Success() {
        Contractor contractor = new Contractor();
        Project project = new Project();
        Activity activity = new Activity();

        when(contractorRepository.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepository.findByProjectCode("PRJ001")).thenReturn(project);
        when(activityRepository.findById("ACT100")).thenReturn(Optional.of(activity));

        String result = logHoursService.logHours(dto);

        assertEquals("Timesheet entry saved as draft.", result);
        verify(timesheetEntryRepository, times(1)).save(any(TimesheetEntry.class));
    }

    @Test
    public void testLogHours_InvalidHours_LessThanZero() {
        dto.setHoursWorked(-1);
        assertThrows(BadRequestException.class, () -> logHoursService.logHours(dto));
    }

    @Test
    public void testLogHours_InvalidHours_GreaterThan24() {
        dto.setHoursWorked(25);
        assertThrows(BadRequestException.class, () -> logHoursService.logHours(dto));
    }

    @Test
    public void testLogHours_ContractorNotFound() {
        when(contractorRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> logHoursService.logHours(dto));
    }

    @Test
    public void testLogHours_ProjectNotFound() {
        Contractor contractor = new Contractor();
        when(contractorRepository.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepository.findByProjectCode("PRJ001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> logHoursService.logHours(dto));
    }

    @Test
    public void testLogHours_ActivityNotFound() {
        Contractor contractor = new Contractor();
        Project project = new Project();

        when(contractorRepository.findById(1)).thenReturn(Optional.of(contractor));
        when(projectRepository.findByProjectCode("PRJ001")).thenReturn(project);
        when(activityRepository.findById("ACT100")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> logHoursService.logHours(dto));
    }
}
