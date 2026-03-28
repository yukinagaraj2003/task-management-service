package com.yukeshkumar.task_management_service.model;

import java.util.UUID;

public class CommentRequest {
    private String comment;
    private UUID projectId;
    public CommentRequest(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}
