package com.matheushdas.taskflowingapi.controller;

import com.matheushdas.taskflowingapi.dto.project.CreateProjectRequest;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
import com.matheushdas.taskflowingapi.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<PagedModel<ProjectResponse>> getProjects(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Sort.Direction sortedDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                sortedDirection,
                "name"
        );

        return ResponseEntity.ok(projectService.findProjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest project) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.save(project));
    }

    @PutMapping
    public ResponseEntity<ProjectResponse> updateProject(@RequestBody UpdateProjectRequest project) {
        return ResponseEntity.ok(projectService.update(project));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<?> finishProject(@PathVariable UUID id) {
        return projectService.finishProject(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<?> reopenProject(@PathVariable UUID id) {
        return projectService.reopenProject(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
