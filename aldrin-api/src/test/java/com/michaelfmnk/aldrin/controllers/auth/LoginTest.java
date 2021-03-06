package com.michaelfmnk.aldrin.controllers.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.michaelfmnk.aldrin.BaseTest;
import com.michaelfmnk.aldrin.dtos.AuthRequest;
import com.michaelfmnk.aldrin.dtos.TokenContainer;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest extends BaseTest {

    @Test
    public void shouldLogin() throws JsonProcessingException {
        AuthRequest authRequest = AuthRequest.builder()
                .login("michaelfmnk@gmail.com")
                .password("test")
                .build();
        TokenContainer container = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract().response().as(TokenContainer.class);


        Headers headers = new Headers(new Header(authProperties.getHeaderName(), container.getToken()));
        given()
                .accept(ContentType.JSON)
                .headers(headers)
                .when()
                .post("/aldrin-api/posts/2/likes")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void shouldNotLoginWithBadCredentials() throws JsonProcessingException {
        AuthRequest authRequest = AuthRequest.builder()
                .login("michaelfmnk@gmail.com")
                .password("badPassword")
                .build();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("title", equalTo("UNAUTHORIZED"))
                .body("status", equalTo(401))
                .body("detail", equalTo("Bad credentials"))
                .body("timestamp", notNullValue())
                .body("dev_message", equalTo("org.springframework.security.authentication.BadCredentialsException"));

        authRequest.setPassword(null);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("title", equalTo("UNAUTHORIZED"))
                .body("status", equalTo(401))
                .body("detail", equalTo("Bad credentials"))
                .body("timestamp", notNullValue())
                .body("dev_message", equalTo("org.springframework.security.authentication.BadCredentialsException"));
    }

    @Test
    public void shouldNotLoginNotEnabledUser() throws JsonProcessingException {
        AuthRequest authRequest = AuthRequest.builder()
                .login("steven@gmail.com")
                .password("test")
                .build();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .body("title", equalTo("METHOD_NOT_ALLOWED"))
                .body("status", equalTo(405))
                .body("detail", equalTo("User is disabled"))
                .body("timestamp", notNullValue())
                .body("dev_message", equalTo("org.springframework.security.authentication.DisabledException"));
    }

    @Test
    public void shouldFailLoginOnUserNotFound() throws JsonProcessingException {
        AuthRequest authRequest = AuthRequest.builder()
                .login("fakeLogin")
                .build();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("title", equalTo("NOT_FOUND"))
                .body("status", equalTo(404))
                .body("detail", equalTo("no user found with login=fakeLogin"))
                .body("timestamp", notNullValue())
                .body("dev_message", equalTo("javax.persistence.EntityNotFoundException"));
    }

    @Test
    public void shouldFailLoginOnUnprocessableEntity() throws JsonProcessingException {
        AuthRequest authRequest = AuthRequest.builder()
                .password("asldkfj;alksdjf;laksjd;flkaj")
                .build();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsBytes(authRequest))
                .when()
                .post("/aldrin-api/auth/login")
                .then()
                .extract().response().prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("title", equalTo("UNPROCESSABLE_ENTITY"))
                .body("status", equalTo(422))
                .body("detail", equalTo("login is not provided"))
                .body("timestamp", notNullValue())
                .body("dev_message", equalTo("org.springframework.security.authentication.InternalAuthenticationServiceException"));

    }

}
