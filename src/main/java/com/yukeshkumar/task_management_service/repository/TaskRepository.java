package com.yukeshkumar.task_management_service.repository;

import com.yukeshkumar.task_management_service.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByProjectId(UUID projectId);
    List<TaskEntity> findByAssignedTo(UUID assignedTo);
}
