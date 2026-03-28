package com.yukeshkumar.task_management_service.model;

import com.yukeshkumar.task_management_service.entity.TaskPriority;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TaskRequest {


        private UUID projectId;
        private String title;
        private String description;
        private TaskPriority priority;
        private UUID assignedTo;
        private Instant dueDate;


        public TaskRequest() {
        }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }
}
