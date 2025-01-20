package com.matheushdas.taskflowingapi.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheushdas.taskflowingapi.dto.auth.LoginRequest;
import com.matheushdas.taskflowingapi.dto.auth.LoginResponse;
import com.matheushdas.taskflowingapi.dto.project.CreateProjectRequest;
import com.matheushdas.taskflowingapi.dto.project.ProjectResponse;
import com.matheushdas.taskflowingapi.dto.project.UpdateProjectRequest;
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
public class ProjectControllerIntegrationTest extends IntegrationTestWithContainers {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static ProjectResponse createdProject;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Nested
    @Order(0)
    class beforeTests {
        @Test
        void authentication() {
            LoginRequest request = new LoginRequest("TestUsername", "testpassword");

            String response = given()
                    .basePath("/auth/login")
                    .port(8888)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post()
                    .then().statusCode(200)
                    .extract().body().as(LoginResponse.class).accessToken();

            specification = new RequestSpecBuilder()
                    .addHeader("Authorization", "Bearer " + response)
                    .setBasePath("/api/project")
                    .setPort(8888)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                    .build();
        }
    }

    @Nested
    @Order(1)
    class createProject {
        @Test
        void shouldCreateAndReturnProjectWhenRequested() throws JsonProcessingException {
            CreateProjectRequest createRequest = new CreateProjectRequest(
                    "TestProjectName0",
                    "TestProjectDescription0"
            );

            String body = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(createRequest)
                    .when().post()
                    .then().statusCode(201)
                    .extract().body().asString();

            ProjectResponse response = objectMapper.readValue(body, ProjectResponse.class);
            createdProject = response;

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(createRequest.name(), response.getName());
            assertEquals(createRequest.description(), response.getDescription());
            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }

    @Nested
    @Order(2)
    class getProjects {
        @Test
        void shouldReturnProjectsPageThatUserParticipatesWhenRequested() throws JsonProcessingException {
            String body = given().spec(specification)
                    .queryParam("page", 0)
                    .queryParam("size", 12)
                    .queryParam("direction", "asc")
                    .when().get()
                    .then().statusCode(200)
                    .extract().body().asString();

            PagedModel<ProjectResponse> projectsPage =
                    objectMapper.readValue(body, new TypeReference<PagedModel<ProjectResponse>>() {});

            for(ProjectResponse project : projectsPage.getContent()) {
                assertNotNull(project.getKey());
                assertNotNull(project.getName());
                assertNotNull(project.getDescription());
                assertNotNull(project.getStatus());
                assertNotNull(project.getLinks());

                assertTrue(project.getName().contains("TestProjectName"));
                assertTrue(project.getDescription().contains("TestProjectDescription"));

                assertEquals("PENDING", project.getStatus());
            }
        }

        @Test
        void shouldReturnProjectsPageWithValidHateoasLinkWhenRequested() throws JsonProcessingException {
            String body = given().spec(specification)
                    .queryParam("page", 0)
                    .queryParam("size", 12)
                    .queryParam("direction", "asc")
                    .when().get()
                    .then().statusCode(200)
                    .extract().body().asString();

            System.out.println(body);

            assertTrue(body.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/api/project?page=0&size=12&direction=asc"));
            assertTrue(body.contains("page\":{\"size\":12,\"totalElements\":1,\"totalPages\":1,\"number\":0"));
        }
    }

    @Nested
    @Order(3)
    class getProjectById {
        @Test
        void shouldReturnProjectWithRequiredIdWhenRequested() throws JsonProcessingException {
            String body = given().spec(specification)
                    .pathParam("id", createdProject.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            ProjectResponse response = objectMapper.readValue(body, ProjectResponse.class);

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(response.getKey(), createdProject.getKey());
            assertEquals("TestProjectName0", response.getName());
            assertEquals("TestProjectDescription0", response.getDescription());
            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }

    @Nested
    @Order(4)
    class updateProject {
        @Test
        void shouldUpdateProjectWithRequiredIdWhenRequested() throws JsonProcessingException {
            UpdateProjectRequest updateRequest = new UpdateProjectRequest(
                    "TestProjectNameUpdated",
                    "TestProjectDescriptionUpdated"
            );

            String body = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", createdProject.getKey())
                    .body(updateRequest)
                    .when().put("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            ProjectResponse response = objectMapper.readValue(body, ProjectResponse.class);

            assertNotNull(response);
            assertNotNull(response.getKey());
            assertNotNull(response.getName());
            assertNotNull(response.getDescription());
            assertNotNull(response.getStatus());
            assertNotNull(response.getLinks());

            assertEquals(response.getKey(), createdProject.getKey());
            assertEquals("TestProjectNameUpdated", response.getName());
            assertEquals("TestProjectDescriptionUpdated", response.getDescription());
            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }

    @Nested
    @Order(5)
    class finishProject {
        @Test
        void shouldFinishProjectWhenRequested() throws JsonProcessingException {
            given().spec(specification)
                    .pathParam("id", createdProject.getKey())
                    .when().patch("/{id}/close")
                    .then().statusCode(204);

            String body = given().spec(specification)
                    .pathParam("id", createdProject.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            ProjectResponse response = objectMapper.readValue(body, ProjectResponse.class);

            assertEquals("FINISHED", response.getStatus());
        }
    }

    @Nested
    @Order(6)
    class reopenProject {
        @Test
        void shouldReopenProjectWhenRequested() throws JsonProcessingException {
            given().spec(specification)
                    .pathParam("id", createdProject.getKey())
                    .when().patch("/{id}/reopen")
                    .then().statusCode(204);

            String body = given().spec(specification)
                    .pathParam("id", createdProject.getKey())
                    .when().get("/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            ProjectResponse response = objectMapper.readValue(body, ProjectResponse.class);

            assertEquals("IN_PROGRESS", response.getStatus());
        }
    }

    @Nested
    @Order(7)
    class afterTests {
        @Test
        void deleteMockProject() {
            given().spec(specification)
                    .basePath("/api/project")
                    .pathParam("id", createdProject.getKey())
                    .when().delete("/{id}")
                    .then().statusCode(204);
        }
    }
}
