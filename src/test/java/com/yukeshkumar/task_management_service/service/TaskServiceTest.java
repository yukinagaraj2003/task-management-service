package com.yukeshkumar.task_management_service.service;

import com.yukeshkumar.task_management_service.client.ProjectClient;
import com.yukeshkumar.task_management_service.entity.TaskCommentEntity;
import com.yukeshkumar.task_management_service.entity.TaskEntity;
import com.yukeshkumar.task_management_service.entity.TaskStatus;
import com.yukeshkumar.task_management_service.exception.AccessDeniedException;
import com.yukeshkumar.task_management_service.exception.BadRequestException;
import com.yukeshkumar.task_management_service.exception.ForbiddenOperationException;
import com.yukeshkumar.task_management_service.exception.ResourceNotFoundException;
import com.yukeshkumar.task_management_service.mapper.TaskMapper;
import com.yukeshkumar.task_management_service.model.*;
import com.yukeshkumar.task_management_service.repository.TaskCommentRepository;
import com.yukeshkumar.task_management_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @InjectMocks
    private TaskService taskService;

    private UUID userId;
    private UUID taskId;
    private UUID projectId;
    private TaskRequest taskRequest;
    private TaskEntity taskEntity;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        taskRequest = new TaskRequest();
        taskRequest.setProjectId(projectId);
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");

        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setCreatedBy(userId);
        taskEntity.setTitle("Test Task");

        taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        taskResponse.setTitle("Test Task");
    }

    @Test
    void createTask_Success() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskMapper.convertDtoToEntity(taskRequest)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(taskRequest, userId);

        assertNotNull(result);
        assertEquals(taskResponse.getId(), result.getId());
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void createTask_ProjectIdNull_ThrowsBadRequestException() {
        taskRequest.setProjectId(null);

        assertThrows(BadRequestException.class, () -> taskService.createTask(taskRequest, userId));
    }

    @Test
    void createTask_UnauthorizedRole_ThrowsAccessDeniedException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("VIEWER");

        assertThrows(AccessDeniedException.class, () -> taskService.createTask(taskRequest, userId));
    }

    @Test
    void getTaskById_Success() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(projectId, taskId);

        assertNotNull(result);
        assertEquals(taskResponse.getId(), result.getId());
    }

    @Test
    void getTaskById_ProjectIdNull_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> taskService.getTaskById(null, taskId));
    }

    @Test
    void getTaskById_UnauthorizedRole_ThrowsAccessDeniedException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("VIEWER");

        assertThrows(AccessDeniedException.class, () -> taskService.getTaskById(projectId, taskId));
    }

    @Test
    void getTaskById_TaskNotFound_ThrowsRuntimeException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(projectId, taskId));
    }

    @Test
    void getAllTask_Success() {
        List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
        List<TaskResponse> taskResponses = Arrays.asList(taskResponse);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findAll()).thenReturn(taskEntities);
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        List<TaskResponse> result = taskService.getAllTask(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskResponse.getId(), result.get(0).getId());
    }

    @Test
    void getAllTask_ProjectIdNull_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> taskService.getAllTask(null));
    }

    @Test
    void getAllTask_UnauthorizedRole_ThrowsAccessDeniedException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("VIEWER");

        assertThrows(AccessDeniedException.class, () -> taskService.getAllTask(projectId));
    }

    @Test
    void getAllTaskByProjectId_Success() {
        List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
        List<TaskResponse> taskResponses = Arrays.asList(taskResponse);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findByProjectId(projectId)).thenReturn(taskEntities);
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        List<TaskResponse> result = taskService.getAllTaskByProjectId(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllTaskByUserId_Success() {
        List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
        List<TaskResponse> taskResponses = Arrays.asList(taskResponse);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findByAssignedTo(userId)).thenReturn(taskEntities);
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        List<TaskResponse> result = taskService.getAllTaskByUserId(userId, projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateTask_Success() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.updateEntityFromDto(taskRequest, taskEntity)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(taskMapper.convertEntityToDto(taskEntity)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(taskRequest, userId, taskId);

        assertNotNull(result);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void updateTask_ProjectIdNull_ThrowsBadRequestException() {
        taskRequest.setProjectId(null);

        assertThrows(BadRequestException.class, () -> taskService.updateTask(taskRequest, userId, taskId));
    }

    @Test
    void updateTask_UnauthorizedRole_ThrowsAccessDeniedException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("MEMBER");

        assertThrows(AccessDeniedException.class, () -> taskService.updateTask(taskRequest, userId, taskId));
    }

    @Test
    void updateTask_TaskNotFound_ThrowsResourceNotFoundException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskRequest, userId, taskId));
    }

    @Test
    void updateTaskStatus_Success() {
        UpdateStatusAndPriority update = new UpdateStatusAndPriority();
        update.setProjectId(projectId);
        update.setStatus(TaskStatus.IN_PROGRESS);

        taskEntity.setAssignedTo(userId);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        taskService.updateTaskStatus(taskId, update, userId);

        verify(taskRepository).save(taskEntity);
        assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus());
    }

    @Test
    void updateTaskStatus_UnauthorizedUser_ThrowsAccessDeniedException() {
        UpdateStatusAndPriority update = new UpdateStatusAndPriority();
        update.setProjectId(projectId);

        taskEntity.setAssignedTo(UUID.randomUUID()); // Different user

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        assertThrows(AccessDeniedException.class, () -> taskService.updateTaskStatus(taskId, update, userId));
    }

    @Test
    void deleteTask_Success() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        taskService.deleteTask(taskId, projectRequest, userId);

        verify(taskRepository).delete(taskEntity);
    }

    @Test
    void deleteTask_UnauthorizedUser_ThrowsAccessDeniedException() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        taskEntity.setCreatedBy(UUID.randomUUID()); // Different user

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(taskId, projectRequest, userId));
    }

    @Test
    void assignTaskToUser_Success() {
        AssignTaskRequest assignRequest = new AssignTaskRequest();
        assignRequest.setUserId(userId);
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        taskService.assignTaskToUser(assignRequest, taskId, projectRequest);

        verify(taskRepository).save(taskEntity);
        assertEquals(userId, taskEntity.getAssignedTo());
    }

    @Test
    void addCommentToTask_Success() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProjectId(projectId);
        commentRequest.setComment("Test Comment");

        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setId(UUID.randomUUID());
        commentEntity.setComment("Test Comment");

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setComment("Test Comment");

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(taskCommentRepository.save(any(TaskCommentEntity.class))).thenReturn(commentEntity);
        when(taskMapper.convertCommentEntityToDto(any(TaskCommentEntity.class))).thenReturn(commentResponse);

        CommentResponse result = taskService.addCommentToTask(commentRequest, taskId, userId);

        assertNotNull(result);
        verify(taskCommentRepository).save(any(TaskCommentEntity.class));
    }

    @Test
    void addCommentToTask_TaskNotFound_ThrowsForbiddenOperationException() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProjectId(projectId);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> taskService.addCommentToTask(commentRequest, taskId, userId));
    }

    @Test
    void getComment_Success() {
        TaskCommentEntity commentEntity = new TaskCommentEntity();
        CommentResponse commentResponse = new CommentResponse();

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskCommentRepository.findByTaskId(taskId)).thenReturn(Optional.of(commentEntity));
        when(taskMapper.convertCommentEntityToDto(commentEntity)).thenReturn(commentResponse);

        CommentResponse result = taskService.getComment(taskId, projectId);

        assertNotNull(result);
    }

    @Test
    void getComment_CommentsNotFound_ThrowsResourceNotFoundException() {
        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskCommentRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getComment(taskId, projectId));
    }

    @Test
    void deleteComment_Success() {
        UUID commentId = UUID.randomUUID();
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setTaskId(taskId);
        commentEntity.setCommentedBy(userId);

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskCommentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));

        taskService.deleteComment(taskId, commentId, projectRequest, userId);

        verify(taskCommentRepository).delete(commentEntity);
    }

    @Test
    void deleteComment_UnauthorizedUser_ThrowsAccessDeniedException() {
        UUID commentId = UUID.randomUUID();
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setTaskId(UUID.randomUUID());
        commentEntity.setCommentedBy(UUID.randomUUID());

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
        when(taskCommentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));

        assertThrows(AccessDeniedException.class, () -> taskService.deleteComment(taskId, commentId, projectRequest, userId));
    }
}
