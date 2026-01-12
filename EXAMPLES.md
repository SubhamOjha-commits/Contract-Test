# 📚 PACT Contract Testing Examples

This document provides practical examples of different contract testing scenarios.

## Table of Contents

1. [Basic GET Request](#basic-get-request)
2. [POST Request with Body](#post-request-with-body)
3. [Query Parameters](#query-parameters)
4. [Error Scenarios](#error-scenarios)
5. [Headers Validation](#headers-validation)
6. [Array Responses](#array-responses)
7. [Nested Objects](#nested-objects)
8. [Optional Fields](#optional-fields)

## Basic GET Request

### Consumer Test

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact getUserPact(PactDslWithProvider builder) {
    return builder
        .given("User exists")
        .uponReceiving("A request to get user")
            .path("/api/users/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("name", "John")
            )
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "getUserPact")
void testGetUser(MockServer mockServer) {
    given()
        .baseUri(mockServer.getUrl())
    .when()
        .get("/api/users/1")
    .then()
        .statusCode(200)
        .body("id", equalTo(1));
}
```

### Provider Test

```java
@State("User exists")
public void userExists() {
    providerStub.addUser(1, "John", "john@example.com");
}
```

## POST Request with Body

### Consumer Test

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact createUserPact(PactDslWithProvider builder) {
    Map<String, String> headers = Map.of("Content-Type", "application/json");
    
    PactDslJsonBody requestBody = new PactDslJsonBody()
        .stringType("name", "Jane")
        .stringType("email", "jane@example.com");
    
    PactDslJsonBody responseBody = new PactDslJsonBody()
        .integerType("id", 2)
        .stringType("name", "Jane")
        .stringType("email", "jane@example.com");
    
    return builder
        .given("User creation allowed")
        .uponReceiving("A request to create user")
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
@PactTestFor(pactMethod = "createUserPact")
void testCreateUser(MockServer mockServer) {
    String requestBody = """
        {
            "name": "Jane",
            "email": "jane@example.com"
        }
        """;
    
    given()
        .baseUri(mockServer.getUrl())
        .contentType("application/json")
        .body(requestBody)
    .when()
        .post("/api/users")
    .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("name", equalTo("Jane"));
}
```

## Query Parameters

### Consumer Test

```java
@Pact(consumer = "ProductConsumer", provider = "ProductProvider")
public V4Pact searchProductsPact(PactDslWithProvider builder) {
    return builder
        .given("Products exist")
        .uponReceiving("A request to search products")
            .path("/api/products")
            .method("GET")
            .query("category=Electronics&minPrice=100")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonArray()
                .object()
                    .integerType("id", 1)
                    .stringType("name", "Laptop")
                    .decimalType("price", 999.99)
                    .stringType("category", "Electronics")
                .closeObject()
            )
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "searchProductsPact")
void testSearchProducts(MockServer mockServer) {
    given()
        .baseUri(mockServer.getUrl())
        .queryParam("category", "Electronics")
        .queryParam("minPrice", 100)
    .when()
        .get("/api/products")
    .then()
        .statusCode(200)
        .body("[0].category", equalTo("Electronics"));
}
```

## Error Scenarios

### 404 Not Found

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact userNotFoundPact(PactDslWithProvider builder) {
    return builder
        .given("User does not exist")
        .uponReceiving("A request for non-existent user")
            .path("/api/users/999")
            .method("GET")
        .willRespondWith()
            .status(404)
            .body(new PactDslJsonBody()
                .stringType("error", "User not found")
                .integerType("status", 404)
            )
        .toPact(V4Pact.class);
}
```

### 400 Bad Request

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact invalidRequestPact(PactDslWithProvider builder) {
    return builder
        .given("Validation enabled")
        .uponReceiving("A request with invalid data")
            .path("/api/users")
            .method("POST")
            .body(new PactDslJsonBody()
                .stringType("name", "")  // Invalid: empty name
            )
        .willRespondWith()
            .status(400)
            .body(new PactDslJsonBody()
                .stringType("error", "Validation failed")
                .array("errors")
                    .stringType("Name is required")
                .closeArray()
            )
        .toPact(V4Pact.class);
}
```

## Headers Validation

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact authenticatedRequestPact(PactDslWithProvider builder) {
    Map<String, String> requestHeaders = Map.of(
        "Authorization", "Bearer token123",
        "Content-Type", "application/json"
    );
    
    Map<String, String> responseHeaders = Map.of(
        "Content-Type", "application/json",
        "X-RateLimit-Remaining", "99"
    );
    
    return builder
        .given("User is authenticated")
        .uponReceiving("An authenticated request")
            .path("/api/users/me")
            .method("GET")
            .headers(requestHeaders)
        .willRespondWith()
            .status(200)
            .headers(responseHeaders)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("name", "John")
            )
        .toPact(V4Pact.class);
}
```

## Array Responses

```java
@Pact(consumer = "ProductConsumer", provider = "ProductProvider")
public V4Pact getProductListPact(PactDslWithProvider builder) {
    PactDslJsonArray products = new PactDslJsonArray()
        .object()
            .integerType("id", 1)
            .stringType("name", "Product 1")
            .decimalType("price", 99.99)
        .closeObject()
        .object()
            .integerType("id", 2)
            .stringType("name", "Product 2")
            .decimalType("price", 149.99)
        .closeObject();
    
    return builder
        .given("Multiple products exist")
        .uponReceiving("A request for product list")
            .path("/api/products")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(products)
        .toPact(V4Pact.class);
}
```

## Nested Objects

```java
@Pact(consumer = "OrderConsumer", provider = "OrderProvider")
public V4Pact getOrderWithDetailsPact(PactDslWithProvider builder) {
    return builder
        .given("Order exists with items")
        .uponReceiving("A request for order details")
            .path("/api/orders/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("status", "PENDING")
                .object("customer")
                    .integerType("id", 100)
                    .stringType("name", "John Doe")
                    .stringType("email", "john@example.com")
                .closeObject()
                .array("items")
                    .object()
                        .integerType("productId", 1)
                        .stringType("name", "Laptop")
                        .integerType("quantity", 1)
                        .decimalType("price", 999.99)
                    .closeObject()
                .closeArray()
                .decimalType("total", 999.99)
            )
        .toPact(V4Pact.class);
}
```

## Optional Fields

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact getUserWithOptionalFieldsPact(PactDslWithProvider builder) {
    return builder
        .given("User with optional fields")
        .uponReceiving("A request for user with optional data")
            .path("/api/users/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("name", "John")
                .stringType("email", "john@example.com")
                // Optional fields
                .stringType("phone")  // Can be null
                .stringType("address")  // Can be null
                .booleanType("verified", true)
            )
        .toPact(V4Pact.class);
}
```

## Advanced: Regex Matching

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact getUserWithRegexPact(PactDslWithProvider builder) {
    return builder
        .given("User exists")
        .uponReceiving("A request with regex validation")
            .path("/api/users/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringMatcher("email", "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", "john@example.com")
                .stringMatcher("phone", "^\\+?[1-9]\\d{1,14}$", "+1234567890")
                .stringMatcher("createdAt", "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$", 
                              "2024-01-01T10:00:00Z")
            )
        .toPact(V4Pact.class);
}
```

---

**Note**: These examples demonstrate various contract testing scenarios. Adapt them to your specific use cases!

