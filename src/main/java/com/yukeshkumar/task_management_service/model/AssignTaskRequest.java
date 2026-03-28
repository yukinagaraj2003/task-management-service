package com.yukeshkumar.task_management_service.model;

import java.util.UUID;

public class AssignTaskRequest {
    private UUID userId;
    public AssignTaskRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
