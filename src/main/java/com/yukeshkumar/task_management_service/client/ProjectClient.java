package com.yukeshkumar.task_management_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "project-management-service", url = "http://localhost:8082/projects")
public interface ProjectClient {
    @GetMapping("/{projectId}/role")
    String getUserRoleForProject(@PathVariable UUID projectId);

}
