package com.tms.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TimesheetSubmissionRequest {
    private Integer contractorId;
    private LocalDate weekStartDate;
    private List<Entry> entries;
 
    public static class Entry {
		public LocalDate date;
        public Integer projectId;
        public String activityCode;
        public double hoursWorked;
        public String comments;
        
        public LocalDate getDate() {
			return date;
		}
		public void setDate(LocalDate date) {
			this.date = date;
		}
		public Integer getProjectId() {
			return projectId;
		}
		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
		public String getActivityCode() {
			return activityCode;
		}
		public void setActivityCode(String activityCode) {
			this.activityCode = activityCode;
		}
		public double getHoursWorked() {
			return hoursWorked;
		}
		public void setHoursWorked(double hoursWorked) {
			this.hoursWorked = hoursWorked;
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}

    }
}