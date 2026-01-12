package com.sdet.pact.consumer.contracts;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Consumer Contract Tests for User Service
 * 
 * This test defines the contract expectations from the consumer's perspective.
 * It generates a PACT file that will be verified by the provider.
 */
@ExtendWith(PactConsumerTestExt.class)
@DisplayName("User Consumer Contract Tests")
public class UserConsumerPactTest {

    private static final String CONSUMER = "UserConsumer";
    private static final String PROVIDER = "UserProvider";

    /**
     * Contract: Get User by ID - Success Scenario
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact getUserByIdPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
            .given("User with ID 1 exists")
            .uponReceiving("A request to get user by ID")
                .path("/api/users/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(new PactDslJsonBody()
                    .integerType("id", 1)
                    .stringType("name", "John Doe")
                    .stringType("email", "john.doe@example.com")
                    .stringType("role", "ADMIN")
                    .booleanType("active", true)
                )
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getUserByIdPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should retrieve user by ID successfully")
    void testGetUserById(au.com.dius.pact.consumer.MockServer mockServer) {
        RequestSpecification request = given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .log().all();

        request
            .when()
                .get("/api/users/1")
            .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", notNullValue())
                .body("email", notNullValue())
                .body("role", notNullValue())
                .body("active", notNullValue());
    }

    /**
     * Contract: Get User by ID - Not Found Scenario
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact getUserByIdNotFoundPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
            .given("User with ID 999 does not exist")
            .uponReceiving("A request to get non-existent user")
                .path("/api/users/999")
                .method("GET")
            .willRespondWith()
                .status(404)
                .headers(headers)
                .body(new PactDslJsonBody()
                    .stringType("error", "User not found")
                    .integerType("status", 404)
                )
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getUserByIdNotFoundPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should return 404 when user not found")
    void testGetUserByIdNotFound(au.com.dius.pact.consumer.MockServer mockServer) {
        given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .log().all()
        .when()
            .get("/api/users/999")
        .then()
            .log().all()
            .statusCode(404)
            .body("error", equalTo("User not found"))
            .body("status", equalTo(404));
    }

    /**
     * Contract: Create User - Success Scenario
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact createUserPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        PactDslJsonBody requestBody = new PactDslJsonBody()
            .stringType("name", "Jane Smith")
            .stringType("email", "jane.smith@example.com")
            .stringType("role", "USER");

        PactDslJsonBody responseBody = new PactDslJsonBody()
            .integerType("id", 2)
            .stringType("name", "Jane Smith")
            .stringType("email", "jane.smith@example.com")
            .stringType("role", "USER")
            .booleanType("active", true);

        return builder
            .given("User creation is allowed")
            .uponReceiving("A request to create a new user")
                .path("/api/users")
                .method("POST")
                .headers(headers)
                .body(requestBody)
            .willRespondWith()
                .status(201)
                .headers(headers)
                .body(responseBody)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createUserPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should create a new user successfully")
    void testCreateUser(au.com.dius.pact.consumer.MockServer mockServer) {
        String requestBody = """
            {
                "name": "Jane Smith",
                "email": "jane.smith@example.com",
                "role": "USER"
            }
            """;

        given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .body(requestBody)
            .log().all()
        .when()
            .post("/api/users")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Jane Smith"))
            .body("email", equalTo("jane.smith@example.com"))
            .body("role", equalTo("USER"))
            .body("active", equalTo(true));
    }

    /**
     * Contract: Update User - Success Scenario
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact updateUserPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        PactDslJsonBody requestBody = new PactDslJsonBody()
            .stringType("name", "John Updated")
            .stringType("email", "john.updated@example.com");

        PactDslJsonBody responseBody = new PactDslJsonBody()
            .integerType("id", 1)
            .stringType("name", "John Updated")
            .stringType("email", "john.updated@example.com")
            .stringType("role", "ADMIN")
            .booleanType("active", true);

        return builder
            .given("User with ID 1 exists")
            .uponReceiving("A request to update user")
                .path("/api/users/1")
                .method("PUT")
                .headers(headers)
                .body(requestBody)
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responseBody)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateUserPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should update user successfully")
    void testUpdateUser(au.com.dius.pact.consumer.MockServer mockServer) {
        String requestBody = """
            {
                "name": "John Updated",
                "email": "john.updated@example.com"
            }
            """;

        given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .body(requestBody)
            .log().all()
        .when()
            .put("/api/users/1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", equalTo("John Updated"))
            .body("email", equalTo("john.updated@example.com"));
    }

    /**
     * Contract: Delete User - Success Scenario
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact deleteUserPact(PactDslWithProvider builder) {
        return builder
            .given("User with ID 1 exists")
            .uponReceiving("A request to delete user")
                .path("/api/users/1")
                .method("DELETE")
            .willRespondWith()
                .status(204)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteUserPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should delete user successfully")
    void testDeleteUser(au.com.dius.pact.consumer.MockServer mockServer) {
        given()
            .baseUri(mockServer.getUrl())
            .log().all()
        .when()
            .delete("/api/users/1")
        .then()
            .log().all()
            .statusCode(204);
    }
}

