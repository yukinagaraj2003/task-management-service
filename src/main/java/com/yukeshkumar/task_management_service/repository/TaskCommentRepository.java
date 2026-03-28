package com.yukeshkumar.task_management_service.repository;

import com.yukeshkumar.task_management_service.entity.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, UUID> {
    Optional<TaskCommentEntity> findByTaskId(UUID taskId);
}
