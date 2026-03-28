package com.yukeshkumar.task_management_service.model;

import com.yukeshkumar.task_management_service.entity.TaskPriority;
import com.yukeshkumar.task_management_service.entity.TaskStatus;

import java.util.UUID;

public class UpdateStatusAndPriority {
    private TaskPriority priority;
    private TaskStatus status;
    private UUID projectId;
    public UpdateStatusAndPriority(){}

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}
