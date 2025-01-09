package com.matheushdas.taskflowingapi.unit.service;

import com.matheushdas.taskflowingapi.dto.project.CreateProjectRequest;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
import com.matheushdas.taskflowingapi.model.entity.Project;
import com.matheushdas.taskflowingapi.model.utility.Status;
import com.matheushdas.taskflowingapi.persistence.ProjectRepository;
import com.matheushdas.taskflowingapi.service.ProjectService;
import com.matheushdas.taskflowingapi.util.mapper.ProjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private Project usedProject;
    private ProjectResponse usedProjectResponse;

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usedProject = new Project(UUID.randomUUID(), "TestProjectName", "TestProjectDescription");
        usedProjectResponse = new ProjectResponse(
                usedProject.getId(),
                usedProject.getName(),
                usedProject.getDescription(),
                Status.IN_PROGRESS.getValue(),
                null
        );
    }

    @Nested
    class HATEOAS {
        @Nested
        class findById {
            @Test
            public void shouldReturnProjectResponseWithHateoasLinkWhenCalledWithValidId() {
                doReturn(Optional.of(usedProject)).when(projectRepository).findById(usedProject.getId());
                doReturn(usedProjectResponse).when(projectMapper).toResponse(usedProject);

                ProjectResponse result = projectService.findById(usedProject.getId());

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getTasks());

                assertEquals(usedProject.getId(), result.getKey());
                assertEquals(usedProject.getName(), result.getName());
                assertEquals(usedProject.getDescription(), result.getDescription());
                assertEquals(Status.IN_PROGRESS.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/project/" + usedProject.getId() + ">;rel=\"self\"]"));
            }
        }

        @Nested
        class save {
            @Test
            public void shouldReturnProjectResponseWithHateoasLinkWhenCalledWithValidProject() {
                CreateProjectRequest createProjectRequest = new CreateProjectRequest(
                        usedProject.getName(),
                        usedProject.getDescription()
                );

                doReturn(usedProject).when(projectMapper).toEntity(createProjectRequest);
                doReturn(usedProject).when(projectRepository).save(usedProject);
                doReturn(usedProjectResponse).when(projectMapper).toResponse(usedProject);

                ProjectResponse result = projectService.save(createProjectRequest);

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getTasks());

                assertEquals(usedProject.getId(), result.getKey());
                assertEquals(usedProject.getName(), result.getName());
                assertEquals(usedProject.getDescription(), result.getDescription());
                assertEquals(Status.IN_PROGRESS.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/project/" + usedProject.getId() + ">;rel=\"self\"]"));
            }
        }

        @Nested
        class update {
            @Test
            public void shouldReturnProjectResponseWithHateoasLinkWhenCalledWithValidIdAndProject() {
                UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest(
                        usedProject.getName(),
                        usedProject.getDescription()
                );

                doReturn(Optional.of(usedProject)).when(projectRepository).findById(usedProject.getId());
                doReturn(usedProject).when(projectRepository).save(usedProject);
                doReturn(usedProjectResponse).when(projectMapper).toResponse(usedProject);

                ProjectResponse result = projectService.update(usedProject.getId(), updateProjectRequest);

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getTasks());

                assertEquals(usedProject.getId(), result.getKey());
                assertEquals(usedProject.getName(), result.getName());
                assertEquals(usedProject.getDescription(), result.getDescription());
                assertEquals(Status.IN_PROGRESS.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/project/" + usedProject.getId() + ">;rel=\"self\"]"));
            }
        }
    }
}
