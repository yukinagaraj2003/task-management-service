package com.yukeshkumar.task_management_service.service;

import com.yukeshkumar.task_management_service.client.ProjectClient;
import com.yukeshkumar.task_management_service.entity.TaskCommentEntity;
import com.yukeshkumar.task_management_service.entity.TaskEntity;
import com.yukeshkumar.task_management_service.entity.TaskStatus;
import com.yukeshkumar.task_management_service.mapper.TaskMapper;
import com.yukeshkumar.task_management_service.model.*;
import com.yukeshkumar.task_management_service.repository.TaskCommentRepository;
import com.yukeshkumar.task_management_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Task Service Integration Tests")
class TaskIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskMapper taskMapper;

    @MockitoBean
    private ProjectClient projectClient;

    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        taskCommentRepository.deleteAll();
        taskRepository.deleteAll();

        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        taskRequest = new TaskRequest();
        taskRequest.setProjectId(projectId);
        taskRequest.setTitle("Integration Test Task");
        taskRequest.setDescription("Integration Test Description");

        when(projectClient.getUserRoleForProject(projectId)).thenReturn("OWNER");
    }

    // ==================== CREATE TASK INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should create task and persist to database")
    void testCreateTask_PersistsToDatabase() {
        TaskResponse response = taskService.createTask(taskRequest, userId);

        assertNotNull(response.getId());
        assertTrue(taskRepository.existsById(response.getId()));

        TaskEntity savedTask = taskRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedTask);
        assertEquals("Integration Test Task", savedTask.getTitle());
        assertEquals(userId, savedTask.getCreatedBy());
        assertEquals(projectId, savedTask.getProjectId());
    }

    @Test
    @DisplayName("Should verify created task in database with all fields")
    void testCreateTask_VerifyAllFields() {
        taskRequest.setDescription("Complete Description");
        TaskResponse response = taskService.createTask(taskRequest, userId);

        TaskEntity savedTask = taskRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedTask);
        assertEquals("Integration Test Task", savedTask.getTitle());
        assertEquals("Complete Description", savedTask.getDescription());
        assertEquals(userId, savedTask.getCreatedBy());
        assertEquals(projectId, savedTask.getProjectId());
        assertNotNull(savedTask.getCreatedAt());
    }

    // ==================== READ TASK INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should retrieve created task from database")
    void testGetTaskById_RetrievesFromDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Get Test Task");
        taskEntity.setDescription("Retrieve me");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        TaskResponse response = taskService.getTaskById(projectId, taskId);

        assertNotNull(response);
        assertEquals("Get Test Task", response.getTitle());
        assertEquals("Retrieve me", response.getDescription());
    }

    @Test
    @DisplayName("Should retrieve all tasks from database")
    void testGetAllTasks_RetrievesAllFromDatabase() {
        TaskEntity task1 = new TaskEntity();
        task1.setProjectId(projectId);
        task1.setTitle("Task 1");
        task1.setCreatedBy(userId);
        taskRepository.save(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setProjectId(projectId);
        task2.setTitle("Task 2");
        task2.setCreatedBy(userId);
        taskRepository.save(task2);

        List<TaskResponse> responses = taskService.getAllTask(projectId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Should retrieve tasks by project ID from database")
    void testGetAllTasksByProjectId_RetrievesFromDatabase() {
        UUID otherProjectId = UUID.randomUUID();

        TaskEntity task1 = new TaskEntity();
        task1.setProjectId(projectId);
        task1.setTitle("Project Task 1");
        task1.setCreatedBy(userId);
        taskRepository.save(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setProjectId(projectId);
        task2.setTitle("Project Task 2");
        task2.setCreatedBy(userId);
        taskRepository.save(task2);

        TaskEntity otherTask = new TaskEntity();
        otherTask.setProjectId(otherProjectId);
        otherTask.setTitle("Other Project Task");
        otherTask.setCreatedBy(userId);
        taskRepository.save(otherTask);

        List<TaskResponse> responses = taskService.getAllTaskByProjectId(projectId);

        assertEquals(2, responses.size());
        assertTrue(responses.stream().allMatch(t -> t.getId() != null));
    }

    @Test
    @DisplayName("Should retrieve tasks by user ID from database")
    void testGetAllTasksByUserId_RetrievesFromDatabase() {
        UUID otherUserId = UUID.randomUUID();

        TaskEntity task1 = new TaskEntity();
        task1.setProjectId(projectId);
        task1.setTitle("User Task 1");
        task1.setAssignedTo(userId);
        task1.setCreatedBy(userId);
        taskRepository.save(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setProjectId(projectId);
        task2.setTitle("User Task 2");
        task2.setAssignedTo(userId);
        task2.setCreatedBy(userId);
        taskRepository.save(task2);

        TaskEntity otherUserTask = new TaskEntity();
        otherUserTask.setProjectId(projectId);
        otherUserTask.setTitle("Other User Task");
        otherUserTask.setAssignedTo(otherUserId);
        otherUserTask.setCreatedBy(userId);
        taskRepository.save(otherUserTask);

        List<TaskResponse> responses = taskService.getAllTaskByUserId(userId, projectId);

        assertEquals(2, responses.size());
    }

    // ==================== UPDATE TASK INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should update task and persist changes to database")
    void testUpdateTask_PersistsChangesToDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Original Title");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setProjectId(projectId);
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");

        taskService.updateTask(updateRequest, userId, taskId);

        TaskEntity updatedTask = taskRepository.findById(taskId).orElse(null);
        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    @DisplayName("Should update task status and persist to database")
    void testUpdateTaskStatus_PersistsToDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Status Task");
        taskEntity.setStatus(TaskStatus.TODO);
        taskEntity.setAssignedTo(userId);
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        UpdateStatusAndPriority updateRequest = new UpdateStatusAndPriority();
        updateRequest.setProjectId(projectId);
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        taskService.updateTaskStatus(taskId, updateRequest, userId);

        TaskEntity updatedTask = taskRepository.findById(taskId).orElse(null);
        assertNotNull(updatedTask);
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    @DisplayName("Should update task status from IN_PROGRESS to DONE")
    void testUpdateTaskStatus_MultipleTransitions() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Status Transition");
        taskEntity.setStatus(TaskStatus.TODO);
        taskEntity.setAssignedTo(userId);
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        // First transition: TODO -> IN_PROGRESS
        UpdateStatusAndPriority update1 = new UpdateStatusAndPriority();
        update1.setProjectId(projectId);
        update1.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTaskStatus(taskId, update1, userId);

        TaskEntity task1 = taskRepository.findById(taskId).orElse(null);
        assertEquals(TaskStatus.IN_PROGRESS, task1.getStatus());

        // Second transition: IN_PROGRESS -> DONE
        UpdateStatusAndPriority update2 = new UpdateStatusAndPriority();
        update2.setProjectId(projectId);
        update2.setStatus(TaskStatus.DONE);
        taskService.updateTaskStatus(taskId, update2, userId);

        TaskEntity task2 = taskRepository.findById(taskId).orElse(null);
        assertEquals(TaskStatus.DONE, task2.getStatus());
    }

    // ==================== DELETE TASK INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should delete task from database")
    void testDeleteTask_DeletesFromDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Delete Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        assertTrue(taskRepository.existsById(taskId));

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        taskService.deleteTask(taskId, projectRequest, userId);

        assertFalse(taskRepository.existsById(taskId));
    }

    // ==================== ASSIGN TASK INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should assign task and persist to database")
    void testAssignTaskToUser_PersistsToDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Assign Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        UUID assigneeId = UUID.randomUUID();
        AssignTaskRequest assignRequest = new AssignTaskRequest();
        assignRequest.setUserId(assigneeId);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        taskService.assignTaskToUser(assignRequest, taskId, projectRequest);

        TaskEntity assignedTask = taskRepository.findById(taskId).orElse(null);
        assertNotNull(assignedTask);
        assertEquals(assigneeId, assignedTask.getAssignedTo());
    }

    @Test
    @DisplayName("Should reassign task to different user")
    void testReassignTask_UpdatesAssignee() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Reassign Task");
        UUID firstAssignee = UUID.randomUUID();
        taskEntity.setAssignedTo(firstAssignee);
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        UUID secondAssignee = UUID.randomUUID();
        AssignTaskRequest assignRequest = new AssignTaskRequest();
        assignRequest.setUserId(secondAssignee);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        taskService.assignTaskToUser(assignRequest, taskId, projectRequest);

        TaskEntity reassignedTask = taskRepository.findById(taskId).orElse(null);
        assertEquals(secondAssignee, reassignedTask.getAssignedTo());
        assertNotEquals(firstAssignee, reassignedTask.getAssignedTo());
    }

    // ==================== COMMENT INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should add comment and persist to database")
    void testAddCommentToTask_PersistsToDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Comment Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProjectId(projectId);
        commentRequest.setComment("Integration Test Comment");

        CommentResponse response = taskService.addCommentToTask(commentRequest, taskId, userId);

        assertNotNull(response);
        assertTrue(taskCommentRepository.existsById(response.getId()));

        TaskCommentEntity savedComment = taskCommentRepository.findById(response.getId()).orElse(null);
        assertNotNull(savedComment);
        assertEquals(taskId, savedComment.getTaskId());
        assertEquals(userId, savedComment.getCommentedBy());
        assertEquals("Integration Test Comment", savedComment.getComment());
    }

    @Test
    @DisplayName("Should retrieve comment from database")
    void testGetComment_RetrievesFromDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Get Comment Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setTaskId(taskId);
        commentEntity.setComment("Retrieve Me");
        commentEntity.setCommentedBy(userId);
        taskCommentRepository.save(commentEntity);

        CommentResponse response = taskService.getComment(taskId, projectId);

        assertNotNull(response);
        assertEquals("Retrieve Me", response.getComment());
    }

    @Test
    @DisplayName("Should delete comment from database")
    void testDeleteComment_DeletesFromDatabase() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Delete Comment Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        TaskCommentEntity commentEntity = new TaskCommentEntity();
        commentEntity.setTaskId(taskId);
        commentEntity.setComment("Delete Me");
        commentEntity.setCommentedBy(userId);
        taskCommentRepository.save(commentEntity);

        UUID commentId = commentEntity.getId();
        assertTrue(taskCommentRepository.existsById(commentId));

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);

        taskService.deleteComment(taskId, commentId, projectRequest, userId);

        assertFalse(taskCommentRepository.existsById(commentId));
    }

    @Test
    @DisplayName("Should add multiple comments to same task")
    void testAddMultipleComments_PersistsAll() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setProjectId(projectId);
        taskEntity.setTitle("Multiple Comments Task");
        taskEntity.setCreatedBy(userId);
        taskRepository.save(taskEntity);

        CommentRequest comment1 = new CommentRequest();
        comment1.setProjectId(projectId);
        comment1.setComment("First Comment");

        CommentRequest comment2 = new CommentRequest();
        comment2.setProjectId(projectId);
        comment2.setComment("Second Comment");

        CommentResponse response1 = taskService.addCommentToTask(comment1, taskId, userId);
        CommentResponse response2 = taskService.addCommentToTask(comment2, taskId, userId);

        assertNotNull(response1.getId());
        assertNotNull(response2.getId());
        assertNotEquals(response1.getId(), response2.getId());
    }

    // ==================== END-TO-END WORKFLOW TESTS ====================

    @Test
    @DisplayName("Should complete full task lifecycle from creation to deletion")
    void testCompleteTaskLifecycle() {
        // 1. Create task
        TaskResponse createdTask = taskService.createTask(taskRequest, userId);
        UUID createdTaskId = createdTask.getId();
        assertTrue(taskRepository.existsById(createdTaskId));

        // 2. Retrieve task
        TaskResponse retrievedTask = taskService.getTaskById(projectId, createdTaskId);
        assertNotNull(retrievedTask);
        assertEquals(createdTaskId, retrievedTask.getId());

        // 3. Add comment
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setProjectId(projectId);
        commentRequest.setComment("Workflow Comment");
        CommentResponse commentResponse = taskService.addCommentToTask(commentRequest, createdTaskId, userId);
        assertTrue(taskCommentRepository.existsById(commentResponse.getId()));

        // 4. Assign task
        UUID assigneeId = UUID.randomUUID();
        AssignTaskRequest assignRequest = new AssignTaskRequest();
        assignRequest.setUserId(assigneeId);
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);
        taskService.assignTaskToUser(assignRequest, createdTaskId, projectRequest);

        // 5. Update status
        assignRequest.setUserId(userId);
        taskService.assignTaskToUser(assignRequest, createdTaskId, projectRequest);
        
        UpdateStatusAndPriority updateRequest = new UpdateStatusAndPriority();
        updateRequest.setProjectId(projectId);
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTaskStatus(createdTaskId, updateRequest, userId);

        // 6. Verify all changes persisted
        TaskEntity verifyTask = taskRepository.findById(createdTaskId).orElse(null);
        assertNotNull(verifyTask);
        assertEquals(TaskStatus.IN_PROGRESS, verifyTask.getStatus());
        assertEquals(userId, verifyTask.getAssignedTo());

        // 7. Delete task
        taskService.deleteTask(createdTaskId, projectRequest, userId);
        assertFalse(taskRepository.existsById(createdTaskId));
    }

    @Test
    @DisplayName("Should handle task creation, update, and deletion sequence")
    void testTaskCRUDSequence() {
        // Create
        TaskResponse created = taskService.createTask(taskRequest, userId);
        UUID taskId = created.getId();
        assertTrue(taskRepository.existsById(taskId));

        // Read
        TaskResponse read = taskService.getTaskById(projectId, taskId);
        assertEquals("Integration Test Task", read.getTitle());

        // Update
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setProjectId(projectId);
        updateRequest.setTitle("Updated Task Title");
        updateRequest.setDescription("Updated Description");
        taskService.updateTask(updateRequest, userId, taskId);

        TaskEntity updated = taskRepository.findById(taskId).orElse(null);
        assertEquals("Updated Task Title", updated.getTitle());

        // Delete
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectID(projectId);
        taskService.deleteTask(taskId, projectRequest, userId);
        assertFalse(taskRepository.existsById(taskId));
    }
}

