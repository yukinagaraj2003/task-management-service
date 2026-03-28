package com.yukeshkumar.task_management_service.model;

import java.time.Instant;
import java.util.UUID;

public class CommentResponse {
    private UUID id;
    private UUID taskID;
    private String comment;
    private UUID commentedBy;
    private Instant commentedAt;
    public CommentResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTaskID() {
        return taskID;
    }

    public void setTaskID(UUID taskID) {
        this.taskID = taskID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(UUID commentedBy) {
        this.commentedBy = commentedBy;
    }

    public Instant getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(Instant commentedAt) {
        this.commentedAt = commentedAt;
    }
}
