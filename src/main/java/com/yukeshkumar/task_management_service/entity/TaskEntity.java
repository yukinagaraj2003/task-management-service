package com.yukeshkumar.task_management_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "tasks")
public class TaskEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name="id",
            columnDefinition = "CHAR(36)",
            nullable = false,
            updatable = false
    )
    private UUID id;
    @Column(name="projectId",columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID projectId;
    @Column(name="title")
    private String title;
    @Column(name="description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name="priority")
    private TaskPriority priority;
    @Column(name="assigned_to",columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private  UUID assignedTo;
    @Column(name="created_at")
    @JsonIgnore
    private Instant createdAt;
    @Column(name="created_by",columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    @JsonIgnore
    private UUID createdBy;
    @Column(name="due_date")
    private Instant dueDate;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    @PrePersist
    public void generateIdAndTimestamps(){
        this.id=UUID.randomUUID();
        this.createdAt=Instant.now();
    }

}
