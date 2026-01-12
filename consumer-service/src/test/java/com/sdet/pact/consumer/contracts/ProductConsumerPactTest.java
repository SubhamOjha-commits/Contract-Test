package com.sdet.pact.consumer.contracts;

import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Consumer Contract Tests for Product Service
 * 
 * Demonstrates contract testing for product-related APIs
 */
@ExtendWith(PactConsumerTestExt.class)
@DisplayName("Product Consumer Contract Tests")
public class ProductConsumerPactTest {

    private static final String CONSUMER = "ProductConsumer";
    private static final String PROVIDER = "ProductProvider";

    /**
     * Contract: Get All Products
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact getAllProductsPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
            .given("Products exist in the system")
            .uponReceiving("A request to get all products")
                .path("/api/products")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(new PactDslJsonArray()
                    .object()
                        .integerType("id", 1)
                        .stringType("name", "Laptop")
                        .decimalType("price", 999.99)
                        .stringType("category", "Electronics")
                        .booleanType("inStock", true)
                    .closeObject()
                    .object()
                        .integerType("id", 2)
                        .stringType("name", "Mouse")
                        .decimalType("price", 29.99)
                        .stringType("category", "Accessories")
                        .booleanType("inStock", true)
                    .closeObject()
                )
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllProductsPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should retrieve all products successfully")
    void testGetAllProducts(au.com.dius.pact.consumer.MockServer mockServer) {
        given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .log().all()
        .when()
            .get("/api/products")
        .then()
            .log().all()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(2)))
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue())
            .body("[0].price", notNullValue())
            .body("[0].category", notNullValue())
            .body("[0].inStock", notNullValue());
    }

    /**
     * Contract: Get Product by ID with Query Parameters
     */
    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public V4Pact getProductByIdWithDetailsPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
            .given("Product with ID 1 exists")
            .uponReceiving("A request to get product with details")
                .path("/api/products/1")
                .method("GET")
                .query("includeDetails=true")
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(new PactDslJsonBody()
                    .integerType("id", 1)
                    .stringType("name", "Laptop")
                    .decimalType("price", 999.99)
                    .stringType("category", "Electronics")
                    .booleanType("inStock", true)
                    .object("details")
                        .stringType("manufacturer", "TechCorp")
                        .stringType("warranty", "2 years")
                        .integerType("stock", 50)
                    .closeObject()
                )
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getProductByIdWithDetailsPact", pactVersion = PactSpecVersion.V4)
    @DisplayName("Should retrieve product with details")
    void testGetProductByIdWithDetails(au.com.dius.pact.consumer.MockServer mockServer) {
        given()
            .baseUri(mockServer.getUrl())
            .contentType("application/json")
            .queryParam("includeDetails", "true")
            .log().all()
        .when()
            .get("/api/products/1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("details.manufacturer", notNullValue())
            .body("details.warranty", notNullValue())
            .body("details.stock", notNullValue());
    }
}

