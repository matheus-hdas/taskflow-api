package com.matheushdas.taskflowingapi.integration.controller;

import com.matheushdas.taskflowingapi.dto.auth.LoginRequest;
import com.matheushdas.taskflowingapi.dto.auth.LoginResponse;
import com.matheushdas.taskflowingapi.dto.auth.RegisterRequest;
import com.matheushdas.taskflowingapi.dto.auth.RegisterResponse;
import com.matheushdas.taskflowingapi.integration.config.IntegrationTestWithContainers;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class AuthControllerIntegrationTest extends IntegrationTestWithContainers {
    private static LoginResponse loginToken;

    @Nested
    @Order(1)
    class register {
        @Test
        public void shouldReturnCreatedUserResponseWhenRequested() {
            RegisterRequest request = new RegisterRequest(
                    "MockAuthTestUsername",
                    "mockauthtest@test.com",
                    "MockAuthTestPassword"
            );

            RegisterResponse response = given()
                    .basePath("/auth/register")
                    .port(8888)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post()
                    .then().statusCode(201)
                    .extract().body().as(RegisterResponse.class);

            assertNotNull(response);

            assertEquals("MockAuthTestUsername", response.username());
            assertEquals("mockauthtest@test.com", response.email());
        }
    }

    @Nested
    @Order(2)
    class login {
        @Test
        public void shouldAuthenticateUserAndReturnAuthTokenWhenRequested() {
            LoginRequest request = new LoginRequest("MockAuthTestUsername", "MockAuthTestPassword");

            loginToken = given()
                    .basePath("/auth/login")
                    .port(8888)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post()
                    .then().statusCode(200)
                    .extract().body().as(LoginResponse.class);

            assertNotNull(loginToken.accessToken());
            assertNotNull(loginToken.refreshToken());

            assertEquals("MockAuthTestUsername", loginToken.username());
            assertEquals(true, loginToken.authenticated());
        }
    }

    @Nested
    @Order(3)
    class refreshToken {
        @Test
        public void shouldRefreshAccessTokenWhenRequested() {
            LoginResponse refreshedToken = given()
                    .basePath("/auth/refresh")
                    .port(8888)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("username", loginToken.username())
                    .header("Authorization", "Bearer " + loginToken.refreshToken())
                    .when().put("{username}")
                    .then().statusCode(200)
                    .extract().body().as(LoginResponse.class);

            assertNotNull(refreshedToken.accessToken());
            assertNotNull(refreshedToken.refreshToken());

            assertEquals("MockAuthTestUsername", refreshedToken.username());
            assertEquals(true, refreshedToken.authenticated());

            assertTrue(loginToken.refreshToken() != refreshedToken.refreshToken());
            assertTrue(loginToken.accessToken() != refreshedToken.accessToken());
        }
    }
}
