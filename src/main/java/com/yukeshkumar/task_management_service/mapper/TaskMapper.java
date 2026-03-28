package com.yukeshkumar.task_management_service.mapper;

import com.yukeshkumar.task_management_service.entity.TaskCommentEntity;
import com.yukeshkumar.task_management_service.entity.TaskEntity;
import com.yukeshkumar.task_management_service.entity.TaskStatus;
import com.yukeshkumar.task_management_service.model.CommentResponse;
import com.yukeshkumar.task_management_service.model.TaskRequest;
import com.yukeshkumar.task_management_service.model.TaskResponse;
import org.springframework.stereotype.Component;

import static com.yukeshkumar.task_management_service.entity.TaskStatus.TODO;

@Component
public class TaskMapper {
    public TaskEntity convertDtoToEntity(TaskRequest taskRequest){
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setPriority(taskRequest.getPriority());
        taskEntity.setTitle(taskRequest.getTitle());
        taskEntity.setDescription(taskRequest.getDescription());
        taskEntity.setDueDate(taskRequest.getDueDate());
        taskEntity.setAssignedTo(taskRequest.getAssignedTo());
        taskEntity.setStatus(TaskStatus.TODO);
        return taskEntity;
    }
    public TaskResponse convertEntityToDto(TaskEntity taskEntity){
        TaskResponse taskResponse=new TaskResponse();
        taskResponse.setId(taskEntity.getId());
        taskResponse.setProjectId(taskEntity.getProjectId());
        taskResponse.setTitle(taskEntity.getTitle());
        taskResponse.setDescription(taskEntity.getDescription());
        taskResponse.setPriority(taskEntity.getPriority());
        taskResponse.setAssignedTo(taskEntity.getAssignedTo());
        taskResponse.setStatus(taskEntity.getStatus());
        return taskResponse;
    }
    public TaskEntity updateEntityFromDto(TaskRequest taskRequest, TaskEntity taskEntity){
        taskEntity.setTitle(taskRequest.getTitle());
        taskEntity.setDescription(taskRequest.getDescription());
        taskEntity.setPriority(taskRequest.getPriority());
        taskEntity.setDueDate(taskRequest.getDueDate());
        taskEntity.setAssignedTo(taskRequest.getAssignedTo());
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        return taskEntity;
    }
    public CommentResponse convertCommentEntityToDto(TaskCommentEntity taskCommentEntity){
        CommentResponse commentResponse=new CommentResponse();
        commentResponse.setId(taskCommentEntity.getId());
        commentResponse.setComment(taskCommentEntity.getComment());
        commentResponse.setTaskID(taskCommentEntity.getTaskId());
        commentResponse.setCommentedBy(taskCommentEntity.getCommentedBy());
        commentResponse.setCommentedAt(taskCommentEntity.getCommentedAt());
        return commentResponse;
    }
}
