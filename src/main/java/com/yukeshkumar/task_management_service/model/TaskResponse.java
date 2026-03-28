package com.yukeshkumar.task_management_service.model;

import com.yukeshkumar.task_management_service.entity.TaskPriority;
import com.yukeshkumar.task_management_service.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public class TaskResponse {


        private UUID id;
        private UUID projectId;
        private String title;
        private String description;
        private TaskStatus status;
        private TaskPriority priority;
        private UUID assignedTo;
        public TaskResponse() {
        }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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


}
