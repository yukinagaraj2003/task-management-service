package com.yukeshkumar.task_management_service.model;

import java.util.UUID;

public class ProjectRequest {
    private UUID projectID;
    public ProjectRequest(){

    }

    public UUID getProjectID() {
        return projectID;
    }

    public void setProjectID(UUID projectID) {
        this.projectID = projectID;
    }
}
