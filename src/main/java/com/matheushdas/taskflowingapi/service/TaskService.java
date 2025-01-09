package com.matheushdas.taskflowingapi.service;

import com.matheushdas.taskflowingapi.controller.TaskController;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
import com.matheushdas.taskflowingapi.dto.task.CreateTaskRequest;
import com.matheushdas.taskflowingapi.dto.task.TaskResponse;
import com.matheushdas.taskflowingapi.dto.task.UpdateTaskRequest;
import com.matheushdas.taskflowingapi.model.utility.Status;
import com.matheushdas.taskflowingapi.persistence.TaskRepository;
import com.matheushdas.taskflowingapi.util.mapper.TaskMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;
    private PagedResourcesAssembler<TaskResponse> resourcesAssembler;

    public PagedModel<TaskResponse> findTasks(Pageable pageable) {
        Page page = taskMapper
                .toResponsePage(taskRepository.findAll(pageable))
                .map(task -> task.add(
                        linkTo(
                                methodOn(TaskController.class)
                                        .getTaskById(task.getKey()))
                                .withSelfRel()));

        Link link = linkTo(
                methodOn(TaskController.class)
                        .getTasks(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                "asc"))
                .withSelfRel();
        return resourcesAssembler.toModel(page, link);
    }

    public TaskResponse findById(UUID id) {
        TaskResponse response = taskMapper
                .toResponse(taskRepository.findById(id).orElseThrow());

        response.add(
                linkTo(
                        methodOn(TaskController.class)
                                .getTaskById(response.getKey()))
                        .withSelfRel());
        return response;
    }

    public TaskResponse save(CreateTaskRequest task) {
        TaskResponse response = taskMapper.toResponse(
                taskRepository.save(
                        taskMapper.toEntity(task)
                )
        );

        response.add(
                linkTo(
                        methodOn(TaskController.class)
                                .getTaskById(response.getKey()))
                        .withSelfRel());
        return response;
    }

    public TaskResponse update(UpdateTaskRequest task) {
        TaskResponse response = taskMapper.toResponse(
                taskRepository.findById(task.id())
                        .map(toUpdate -> {
                            toUpdate.setName(task.name());
                            toUpdate.setDescription(task.description());
                            return taskRepository.save(toUpdate);
                        }).orElseThrow()
        );

        response.add(
                linkTo(
                        methodOn(TaskController.class)
                                .getTaskById(response.getKey()))
                        .withSelfRel());
        return response;
    }

    @Transactional
    public boolean startTask(UUID id) {
        return taskRepository
                .changeStateByTaskId(id, Status.IN_PROGRESS.getValue()) == 1;
    }

    @Transactional
    public boolean finishTask(UUID id) {
        return taskRepository
                .changeStateByTaskId(id, Status.CLOSED.getValue()) == 1;
    }

    @Transactional
    public boolean reopenTask(UUID id) {
        return startTask(id);
    }

    public void delete(UUID id) {
        if(taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }
    }
}
