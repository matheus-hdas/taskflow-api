package com.matheushdas.taskflowingapi.controller;

import com.matheushdas.taskflowingapi.dto.task.CreateTaskRequest;
import com.matheushdas.taskflowingapi.dto.task.TaskResponse;
import com.matheushdas.taskflowingapi.dto.task.UpdateTaskRequest;
import com.matheushdas.taskflowingapi.service.TaskService;
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
@RequestMapping("/api/task")
public class TaskController {
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<PagedModel<TaskResponse>> getTasks(
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

        return ResponseEntity.ok(taskService.findTasks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest task) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.save(task));
    }

    @PutMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestBody UpdateTaskRequest task) {
        return ResponseEntity.ok(taskService.update(task));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startTask(@PathVariable UUID id) {
        return taskService.startTask(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<?> finishTask(@PathVariable UUID id) {
        return taskService.finishTask(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<?> reopenTask(@PathVariable UUID id) {
        return taskService.reopenTask(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
