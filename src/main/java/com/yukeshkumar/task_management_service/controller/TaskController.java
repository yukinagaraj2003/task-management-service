package com.yukeshkumar.task_management_service.controller;

import com.yukeshkumar.task_management_service.model.*;
import com.yukeshkumar.task_management_service.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        TaskResponse taskResponse = taskService.createTask(request, userId);
        System.out.println("AssignedTo from request: " + request.getAssignedTo());
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @RequestParam UUID projectId,
            @PathVariable UUID taskId
    ) {
        TaskResponse taskResponse = taskService.getTaskById(projectId, taskId);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTask(@RequestParam UUID projectId) {
        List<TaskResponse> taskResponses = taskService.getAllTask(projectId);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }

    @GetMapping("project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getAllTaskByProjectId(@PathVariable UUID projectId) {
        List<TaskResponse> taskResponses = taskService.getAllTaskByProjectId(projectId);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getAllTaskByUserId(@PathVariable UUID userId, @RequestParam UUID projectId) {
        List<TaskResponse> taskResponses = taskService.getAllTaskByUserId(userId, projectId);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @RequestBody TaskRequest request,
            @PathVariable UUID taskId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        TaskResponse taskResponse = taskService.updateTask(request, userId, taskId);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@RequestBody UpdateStatusAndPriority request, @PathVariable UUID taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        taskService.updateTaskStatus(taskId,request,userId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID taskId, @RequestBody ProjectRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        taskService.deleteTask(taskId, request,userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/projects/{projectId}/tasks/{taskId}/assign")
    public ResponseEntity<?> assignTaskToUser(@RequestBody AssignTaskRequest request, @PathVariable UUID taskId, @PathVariable UUID projectId) {
        taskService.assignTaskToUser(request, taskId,projectId );
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> addCommentToTask(
            @RequestBody CommentRequest request,
            @PathVariable UUID taskId,
            @RequestParam UUID projectId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        CommentResponse response = taskService.addCommentToTask(request, taskId, projectId, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getComment(
            @PathVariable("taskId") UUID taskId,
            @RequestParam(name = "projectId", required = true) UUID projectId
    ) {
        List<CommentResponse> response = taskService.getComment(taskId, projectId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @RequestBody ProjectRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        taskService.deleteComment(taskId, commentId, request, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
