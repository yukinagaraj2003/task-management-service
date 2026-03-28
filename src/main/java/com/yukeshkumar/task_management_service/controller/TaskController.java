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
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        TaskResponse taskResponse = taskService.createTask(request, userId);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}?p")
    public ResponseEntity<TaskResponse> getTaskById(@RequestBody ProjectRequest request, @PathVariable UUID taskId) {
        TaskResponse taskResponse = taskService.getTaskById(request, taskId);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTask(@RequestBody ProjectRequest request) {
        List<TaskResponse> taskResponses = taskService.getAllTask(request);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskResponse>> getAllTaskByProjectId(@PathVariable UUID projectId) {
        List<TaskResponse> taskResponses = taskService.getAllTaskByProjectId(projectId);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getAllTaskByUserId(@PathVariable UUID userId, @RequestBody ProjectRequest request) {
        List<TaskResponse> taskResponses = taskService.getAllTaskByUserId(userId, request);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskRequest request, @PathVariable UUID taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        TaskResponse taskResponse = taskService.updateTask(request, taskId, userId);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@RequestBody UpdateStatusAndPriority request, @PathVariable UUID taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        taskService.updateTaskStatus(taskId,request,userId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID taskId, @RequestBody ProjectRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        taskService.deleteTask(taskId, request,userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignTaskToUser(@RequestBody AssignTaskRequest request, @PathVariable UUID taskId, @RequestBody ProjectRequest projectRequest) {
        taskService.assignTaskToUser(request, taskId, projectRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> addCommentToTask(@RequestBody CommentRequest request, @PathVariable UUID taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
       CommentResponse response= taskService.addCommentToTask(request, taskId,userId);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> getComment(@PathVariable UUID taskId,@RequestBody ProjectRequest request){
        CommentResponse response= taskService.getComment(taskId,request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID taskId,@PathVariable UUID commentId,@RequestBody ProjectRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();
        taskService.deleteComment(taskId, commentId, request, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
