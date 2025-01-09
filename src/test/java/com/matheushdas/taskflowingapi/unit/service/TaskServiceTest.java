package com.matheushdas.taskflowingapi.unit.service;

import com.matheushdas.taskflowingapi.dto.task.CreateTaskRequest;
import com.matheushdas.taskflowingapi.dto.task.TaskResponse;
import com.matheushdas.taskflowingapi.model.entity.Task;
import com.matheushdas.taskflowingapi.model.utility.Status;
import com.matheushdas.taskflowingapi.persistence.TaskRepository;
import com.matheushdas.taskflowingapi.service.TaskService;
import com.matheushdas.taskflowingapi.util.mapper.TaskMapper;
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
public class TaskServiceTest {
    private Task usedTask;
    private TaskResponse usedTaskResponse;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usedTask = new Task(
                UUID.randomUUID(),
                "TestTaskName",
                "TestTaskDescription",
                null
        );

        usedTaskResponse = new TaskResponse(
                usedTask.getId(),
                usedTask.getName(),
                usedTask.getDescription(),
                Status.PENDING.getValue(),
                null
        );
    }

    @Nested
    class HATEOAS {
        @Nested
        class findById {
            @Test
            void shouldReturnTaskResponseWithHateoasLinkWhenCalledWithValidId() {
                doReturn(Optional.of(usedTask)).when(taskRepository).findById(usedTask.getId());
                doReturn(usedTaskResponse).when(taskMapper).toResponse(usedTask);

                TaskResponse result = taskService.findById(usedTask.getId());

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getProject());

                assertEquals(usedTask.getId(), result.getKey());
                assertEquals(usedTask.getName(), result.getName());
                assertEquals(usedTask.getDescription(), result.getDescription());
                assertEquals(Status.PENDING.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/task/" + usedTask.getId() + ">;rel=\"self\"]"));
            }
        }

        @Nested
        class save {
            @Test
            void shouldReturnTaskResponseWithHateoasLinkWhenCalledWithValidTask() {
                CreateTaskRequest createTaskRequest = new CreateTaskRequest(
                        "TestTaskName",
                        "TestTaskDescription",
                        null
                );

                doReturn(usedTask).when(taskMapper).toEntity(createTaskRequest);
                doReturn(usedTask).when(taskRepository).save(usedTask);
                doReturn(usedTaskResponse).when(taskMapper).toResponse(usedTask);

                TaskResponse result = taskService.save(createTaskRequest);

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getProject());

                assertEquals(usedTask.getId(), result.getKey());
                assertEquals(usedTask.getName(), result.getName());
                assertEquals(usedTask.getDescription(), result.getDescription());
                assertEquals(Status.PENDING.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/task/" + usedTask.getId() + ">;rel=\"self\"]"));
            }
        }

        @Nested
        class update {
            @Test
            void shouldReturnTaskResponseWithHateoasLinkWhenCalledWithValidTask() {
                CreateTaskRequest createTaskRequest = new CreateTaskRequest(
                        "TestTaskName",
                        "TestTaskDescription",
                        null
                );

                doReturn(usedTask).when(taskMapper).toEntity(createTaskRequest);
                doReturn(usedTask).when(taskRepository).save(usedTask);
                doReturn(usedTaskResponse).when(taskMapper).toResponse(usedTask);

                TaskResponse result = taskService.save(createTaskRequest);

                assertNotNull(result);
                assertNotNull(result.getKey());
                assertNotNull(result.getName());
                assertNotNull(result.getDescription());
                assertNotNull(result.getStatus());
                assertNotNull(result.getLinks());

                assertNull(result.getProject());

                assertEquals(usedTask.getId(), result.getKey());
                assertEquals(usedTask.getName(), result.getName());
                assertEquals(usedTask.getDescription(), result.getDescription());
                assertEquals(Status.PENDING.getValue(), result.getStatus());

                assertTrue(result.toString().contains("links: [</api/task/" + usedTask.getId() + ">;rel=\"self\"]"));
            }
        }
    }
}
