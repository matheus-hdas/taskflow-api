package com.matheushdas.taskflowingapi.service;

import com.matheushdas.taskflowingapi.controller.ProjectController;
import com.matheushdas.taskflowingapi.dto.project.CreateProjectRequest;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
import com.matheushdas.taskflowingapi.model.utility.Status;
import com.matheushdas.taskflowingapi.persistence.ProjectRepository;
import com.matheushdas.taskflowingapi.util.mapper.ProjectMapper;
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
public class ProjectService {
    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;
    private PagedResourcesAssembler<ProjectResponse> resourcesAssembler;

    public PagedModel<ProjectResponse> findProjects(Pageable pageable) {
        Page page = projectMapper
                .toResponsePage(projectRepository.findAll(pageable))
                .map(project -> project.add(
                        linkTo(
                                methodOn(ProjectController.class)
                                        .getProjectById(project.getKey()))
                                .withSelfRel()));

        Link link = linkTo(
                methodOn(ProjectController.class)
                        .getProjects(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                "asc"))
                .withSelfRel();
        return resourcesAssembler.toModel(page, link);
    }

    public ProjectResponse findById(UUID id) {
        ProjectResponse response = projectMapper
                .toResponse(projectRepository.findById(id).orElseThrow());

        response.add(
                linkTo(methodOn(ProjectController.class)
                        .getProjectById(response.getKey()))
                        .withSelfRel());

        return response;
    }

    public ProjectResponse save(CreateProjectRequest project) {
        ProjectResponse response = projectMapper.toResponse(
                projectRepository.save(
                        projectMapper.toEntity(project)
                )
        );

        response.add(
                linkTo(methodOn(ProjectController.class)
                                .getProjectById(response.getKey()))
                        .withSelfRel());

        return response;
    }

    public ProjectResponse update(UpdateProjectRequest project) {
        ProjectResponse response = projectMapper.toResponse(
                projectRepository.findById(project.id())
                        .map(toUpdate -> {
                            toUpdate.setName(project.name());
                            toUpdate.setDescription(project.description());
                            return projectRepository.save(toUpdate);
                        }).orElseThrow()
        );
        response.add(
                linkTo(methodOn(ProjectController.class)
                        .getProjectById(response.getKey()))
                        .withSelfRel());

        return response;
    }

    @Transactional
    public boolean finishProject(UUID id) {
        return projectRepository
                .changeStateByProjectId(id, Status.CLOSED.getValue()) == 1;
    }

    @Transactional
    public boolean reopenProject(UUID id) {
        return projectRepository
                .changeStateByProjectId(id, Status.IN_PROGRESS.getValue()) == 1;
    }

    public void delete(UUID id) {
        if(projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
    }
}
