package com.matheushdas.taskflowingapi.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.task.CreateTaskRequest;
import com.matheushdas.taskflowingapi.dto.task.TaskResponse;
import com.matheushdas.taskflowingapi.dto.task.UpdateTaskRequest;
import com.matheushdas.taskflowingapi.integration.config.IntegrationTestWithContainers;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TaskControllerIntegrationTest extends IntegrationTestWithContainers {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static ProjectResponse createdProject;
    private static TaskResponse createdTask;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/task")
                .setPort(8888)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Nested
    @Order(0)
    class beforeTests {
        @Test
        void createMockProject() throws JsonProcessingException {
            String body = given().spec(specification)
                    .basePath("/api/project")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"name\":\"MockForTaskRouteTest\",\"description\":\"Any Description\"}")
                    .when().post()
                    .then().statusCode(201)
                    .extract().body().asString();

            createdProject = objectMapper.readValue(body, ProjectResponse.class);
        }
    }

    @Nested
    @Order(1)
    class createTask {
        @Test
        void shouldCreateAndReturnTaskWhenRequested() throws JsonProcessingException {
            CreateTaskRequest createRequest = new CreateTaskRequest(
                    "TestTaskName0",
                    "TestTaskDescription0",
                    createdProject.getKey()
            );

            String body = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(createRequest)
                    .when().post()
                    .then().statusCode(201)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);
            createdTask = response;

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(createRequest.name(), response.getName());
            assertEquals(createRequest.description(), response.getDescription());
            assertEquals("PENDING", response.getStatus());
        }
    }

    @Nested
    @Order(2)
    class getTasks {
        @Test
        void shouldReturnTasksPageThatUserParticipatesWhenRequested() throws JsonProcessingException {
            String body = given().spec(specification)
                    .queryParam("page", 0)
                    .queryParam("size", 12)
                    .queryParam("direction", "asc")
                    .when().get()
                    .then().statusCode(200)
                    .extract().body().asString();

            PagedModel<TaskResponse> tasksPage =
                    objectMapper.readValue(body, new TypeReference<PagedModel<TaskResponse>>() {});

            assertNotNull(tasksPage);
            assertNotNull(tasksPage.getContent());

            for(TaskResponse task : tasksPage.getContent()) {
                assertNotNull(task.getKey());
                assertNotNull(task.getName());
                assertNotNull(task.getDescription());
                assertNotNull(task.getStatus());
                assertNotNull(task.getLinks());

                assertTrue(task.getName().contains("TestTaskName"));
                assertTrue(task.getDescription().contains("TestTaskDescription"));

                assertEquals("PENDING", task.getStatus());
            }
        }
    }

    @Nested
    @Order(3)
    class getTaskById {
        @Test
        void shouldReturnTaskWithRequiredIdWhenRequested() throws JsonProcessingException {
            String body = given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(response.getKey(), createdTask.getKey());
            assertEquals("TestTaskName0", response.getName());
            assertEquals("TestTaskDescription0", response.getDescription());
            assertEquals("PENDING", response.getStatus());
        }
    }

    @Nested
    @Order(4)
    class updateTask {
        @Test
        void shouldUpdateTaskWhenRequested() throws JsonProcessingException {
            UpdateTaskRequest updateRequest = new UpdateTaskRequest(
                    "TestTaskNameUpdated",
                    "TestTaskDescriptionUpdated"
            );

            String body = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", createdTask.getKey())
                    .body(updateRequest)
                    .when().put("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(response.getKey(), createdTask.getKey());
            assertEquals("TestTaskNameUpdated", response.getName());
            assertEquals("TestTaskDescriptionUpdated", response.getDescription());
            assertEquals("PENDING", response.getStatus());
        }
    }

    @Nested
    @Order(5)
    class startTask {
        @Test
        void shouldStartTaskWhenRequested() throws JsonProcessingException {
            given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().patch("/{id}/start")
                    .then().statusCode(204);

            String body = given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);

            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }

    @Nested
    @Order(6)
    class finishTask {
        @Test
        void shouldFinishTaskWhenRequested() throws JsonProcessingException {
            given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().patch("/{id}/close")
                    .then().statusCode(204);

            String body = given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);

            assertEquals("FINISHED", response.getStatus());
        }
    }

    @Nested
    @Order(7)
    class reopenTask {
        @Test
        void shouldReopenTaskWhenRequested() throws JsonProcessingException {
            given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().patch("/{id}/reopen")
                    .then().statusCode(204);

            String body = given().spec(specification)
                    .pathParam("id", createdTask.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            TaskResponse response = objectMapper.readValue(body, TaskResponse.class);

            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }
}
