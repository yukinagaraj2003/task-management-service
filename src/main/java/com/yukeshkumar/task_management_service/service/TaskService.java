package com.yukeshkumar.task_management_service.service;

import com.yukeshkumar.task_management_service.client.ProjectClient;
import com.yukeshkumar.task_management_service.entity.TaskCommentEntity;
import com.yukeshkumar.task_management_service.entity.TaskEntity;
import com.yukeshkumar.task_management_service.exception.AccessDeniedException;
import com.yukeshkumar.task_management_service.exception.BadRequestException;
import com.yukeshkumar.task_management_service.exception.ForbiddenOperationException;
import com.yukeshkumar.task_management_service.exception.ResourceNotFoundException;
import com.yukeshkumar.task_management_service.mapper.TaskMapper;
import com.yukeshkumar.task_management_service.model.*;
import com.yukeshkumar.task_management_service.repository.TaskCommentRepository;
import com.yukeshkumar.task_management_service.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectClient projectClient;
    private final TaskCommentRepository taskCommentRepository;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, ProjectClient projectClient, TaskCommentRepository taskCommentRepository) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectClient = projectClient;
    }

    public TaskResponse createTask(TaskRequest request, UUID userId) {
        if (request.getProjectId() == null) {
            throw new BadRequestException("ProjectId is required");
        }
        String role = projectClient.getUserRoleForProject(request.getProjectId());
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to create task for this project");
        }
        TaskEntity taskEntity = taskMapper.convertDtoToEntity(request);
        taskEntity.setProjectId(request.getProjectId());
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);
        return taskMapper.convertEntityToDto(taskEntity);


    }

    public TaskResponse getTaskById(ProjectRequest request, UUID taskId) {
        if (request.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to view task for this project");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.convertEntityToDto(taskEntity);
    }

    public List<TaskResponse> getAllTask(ProjectRequest request) {
        if (request.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to view task for this project");
        }
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (TaskEntity taskEntity : taskEntities) {
            taskResponses.add(taskMapper.convertEntityToDto(taskEntity));

        }
        return taskResponses;
    }

    public List<TaskResponse> getAllTaskByProjectId(UUID projectId) {
        if (projectId == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(projectId);
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to view task for this project");
        }
        List<TaskEntity> taskEntities = taskRepository.findByProjectId(projectId);
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (TaskEntity taskEntity : taskEntities) {
            taskResponses.add(taskMapper.convertEntityToDto(taskEntity));

        }
        return taskResponses;
    }

    public List<TaskResponse> getAllTaskByUserId(UUID userId, ProjectRequest request) {
        if (request.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to view task for this project");
        }
        List<TaskEntity> taskEntities = taskRepository.findByAssignedTo(userId);
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (TaskEntity taskEntity : taskEntities) {
            taskResponses.add(taskMapper.convertEntityToDto(taskEntity));

        }
        return taskResponses;

    }

    public TaskResponse updateTask(TaskRequest request, UUID userId, UUID taskId) {
        if (request.getProjectId() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectId());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to update task for this project");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!taskEntity.getCreatedBy().equals(userId)&& !taskEntity.getAssignedTo().equals(request.getAssignedTo())) {
            throw new AccessDeniedException("User not authorized to update this task");
        }
        TaskEntity updatedTask = taskMapper.updateEntityFromDto(request, taskEntity);
        updatedTask.setCreatedBy(userId);
        updatedTask.setProjectId(request.getProjectId());
        taskRepository.save(updatedTask);
        return taskMapper.convertEntityToDto(updatedTask);

    }

    public void updateTaskStatus(UUID taskId, UpdateStatusAndPriority update, UUID userId) {
        if (update.getProjectId() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(update.getProjectId());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to update task for this project");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!taskEntity.getAssignedTo().equals(userId)) {
            throw new AccessDeniedException("User not authorized to update this task");
        }
        taskEntity.setStatus(update.getStatus());

        taskRepository.save(taskEntity);

    }

    public void deleteTask(UUID taskId, ProjectRequest request, UUID userId) {
        if (request.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to delete task for this project");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!taskEntity.getCreatedBy().equals(userId)) {
            throw new AccessDeniedException("User not authorized to delete this task");
        }
        taskRepository.delete(taskEntity);
    }

    public void assignTaskToUser(AssignTaskRequest request, UUID taskId, ProjectRequest projectRequest) {
        if (projectRequest.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(projectRequest.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to assign task for this project");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        taskEntity.setAssignedTo(request.getUserId());
        taskRepository.save(taskEntity);
    }

    public CommentResponse addCommentToTask(CommentRequest request, UUID taskId,  UUID userId) {
        if (request.getProjectId() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(request.getProjectId());
        if (!role.equals("OWNER") && !role.equals("MEMBER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to comment on task for this project");
        }
        if(!taskRepository.existsById(taskId)){
            throw new ForbiddenOperationException("Task not found");
        }
        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setTaskId(taskId);
        commentEntity.setComment(request.getComment());
        commentEntity.setCommentedBy(userId);
        taskCommentRepository.save(commentEntity);
        return taskMapper.convertCommentEntityToDto(commentEntity);

    }
    public CommentResponse getComment(UUID taskId, ProjectRequest projectRequest) {
        if (projectRequest.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(projectRequest.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to view comments for this project");
        }
        TaskCommentEntity commentEntity = taskCommentRepository.findByTaskId(taskId).orElseThrow(() -> new ResourceNotFoundException("Comments not found"));
        return taskMapper.convertCommentEntityToDto(commentEntity);
    }
    public void deleteComment(UUID taskId, UUID commentId, ProjectRequest projectRequest, UUID userId) {
        if (projectRequest.getProjectID() == null) {
            throw new BadRequestException("ProjectId is required");

        }
        String role = projectClient.getUserRoleForProject(projectRequest.getProjectID());
        if (!role.equals("OWNER") && !role.equals("MAINTAINER")) {
            throw new AccessDeniedException("User not authorized to delete comments for this project");
        }
        TaskCommentEntity commentEntity = taskCommentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!commentEntity.getTaskId().equals(taskId)&&!commentEntity.getCommentedBy().equals(userId)) {
            throw new AccessDeniedException("User not authorized to delete this comment");
        }
        taskCommentRepository.delete(commentEntity);
    }

}


