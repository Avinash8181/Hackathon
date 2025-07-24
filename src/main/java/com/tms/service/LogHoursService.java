package com.tms.service;

import com.tms.dto.LogHoursDTO;
import org.springframework.stereotype.Service;

@Service
public interface LogHoursService {
    String logHours(LogHoursDTO dto);
}
