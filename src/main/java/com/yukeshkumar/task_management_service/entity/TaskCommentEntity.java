package com.yukeshkumar.task_management_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "task_comments")
public class TaskCommentEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "id",
            columnDefinition = "CHAR(36)",
            nullable = false,
            updatable = false
    )
    private UUID id;
    @Column(name="task_id",columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID taskId;
    @Column(name="comment")
    private String comment;
    @Column(name="commented_by",columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID commentedBy;
    @Column(name="commented_at")
    private Instant commentedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
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

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.commentedAt = Instant.now();
    }


}
