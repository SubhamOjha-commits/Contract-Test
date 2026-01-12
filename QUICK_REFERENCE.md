# 🚀 Quick Reference Card

## Essential Commands

### Setup & Start
```bash
# Initial setup
make setup

# Start Pact Broker
make start-broker
# OR
docker-compose up -d

# Verify broker is running
curl http://localhost:9292/diagnostic/status/heartbeat
```

### Running Tests
```bash
# Run all tests
make test

# Consumer tests only
make consumer-test
# OR
cd consumer-service && mvn test

# Provider tests only
make provider-test
# OR
cd provider-service && mvn test

# Specific test class
mvn test -Dtest=UserConsumerPactTest
```

### Pact Broker Operations
```bash
# Publish contracts
make publish

# Check broker status
make status

# View logs
make logs

# Stop broker
make stop-broker
```

## Key Annotations

### Consumer Side
```java
@ExtendWith(PactConsumerTestExt.class)  // Enable PACT for JUnit 5
@Pact(consumer = "Name", provider = "Name")  // Define contract
@PactTestFor(pactMethod = "methodName")  // Link test to contract
```

### Provider Side
```java
@Provider("ProviderName")  // Identify provider
@PactFolder("path/to/pacts")  // Location of PACT files
@State("state description")  // Setup provider state
@TestTemplate  // Run for each interaction
@ExtendWith(PactVerificationInvocationContextProvider.class)
```

## REST Assured Patterns

### Basic Request
```java
given()
    .baseUri(url)
    .contentType("application/json")
.when()
    .get("/api/resource")
.then()
    .statusCode(200)
    .body("field", equalTo(value));
```

### With Body
```java
given()
    .body(requestBody)
.when()
    .post("/api/resource")
.then()
    .statusCode(201);
```

### With Query Params
```java
given()
    .queryParam("key", "value")
.when()
    .get("/api/resource")
```

### With Headers
```java
given()
    .header("Authorization", "Bearer token")
.when()
    .get("/api/resource")
```

## PACT DSL Patterns

### Simple Body
```java
new PactDslJsonBody()
    .integerType("id", 1)
    .stringType("name", "John")
    .booleanType("active", true)
```

### Array
```java
new PactDslJsonArray()
    .object()
        .integerType("id", 1)
        .stringType("name", "Item 1")
    .closeObject()
```

### Nested Object
```java
new PactDslJsonBody()
    .integerType("id", 1)
    .object("nested")
        .stringType("field", "value")
    .closeObject()
```

### With Regex
```java
.stringMatcher("email", "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", "test@example.com")
```

## Common Matchers

```java
// Type matching (recommended)
.integerType("id", 1)
.stringType("name", "example")
.decimalType("price", 99.99)
.booleanType("active", true)

// Exact matching
.integerValue("id", 1)
.stringValue("name", "exact")

// Regex matching
.stringMatcher("field", "regex", "example")

// Date/Time
.datetime("timestamp", "yyyy-MM-dd'T'HH:mm:ss")
```

## Provider States

```java
@State("User with ID 1 exists")
public void userExists() {
    // Setup test data
    stub.addUser(1, "John", "john@example.com");
}

@State("User does not exist")
public void userNotExists() {
    // Ensure user doesn't exist
    stub.removeUser(999);
}
```

## File Locations

```
consumer-service/target/pacts/          # Generated PACT files
provider-service/target/surefire-reports/  # Test reports
docker-compose.yml                      # Pact Broker config
.github/workflows/                      # CI/CD pipelines
```

## URLs

- **Pact Broker**: http://localhost:9292
- **HAL Browser**: http://localhost:9292/hal-browser/browser.html
- **Health Check**: http://localhost:9292/diagnostic/status/heartbeat

## Troubleshooting

### Broker not starting
```bash
docker-compose down -v
docker-compose up -d
```

### Tests can't find PACT files
```bash
# Run consumer tests first
cd consumer-service && mvn test
# Then provider tests
cd ../provider-service && mvn test
```

### Port already in use
```bash
lsof -i :9292
kill -9 <PID>
```

### Maven cache issues
```bash
rm -rf ~/.m2/repository/au/com/dius/pact
mvn clean install -U
```

## CI/CD Workflow

1. **Consumer changes** → Consumer tests run → PACT published
2. **Provider changes** → Provider verification runs
3. **Can-I-Deploy** → Check compatibility → Deploy if safe

## Best Practices Checklist

- [ ] Use type matchers, not exact values
- [ ] Keep contracts minimal and focused
- [ ] One interaction per test
- [ ] Implement all provider states
- [ ] Version your contracts
- [ ] Run verification on every change
- [ ] Use Pact Broker for storage
- [ ] Automate in CI/CD
- [ ] Document breaking changes
- [ ] Test error scenarios

## Quick Test Template

### Consumer Test
```java
@Pact(consumer = "Consumer", provider = "Provider")
public V4Pact createPact(PactDslWithProvider builder) {
    return builder
        .given("state")
        .uponReceiving("description")
            .path("/api/resource")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .stringType("field", "value")
            )
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "createPact")
void test(MockServer mockServer) {
    given().baseUri(mockServer.getUrl())
    .when().get("/api/resource")
    .then().statusCode(200);
}
```

### Provider Test
```java
@Provider("Provider")
@PactFolder("../consumer/target/pacts")
public class ProviderTest {
    
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verify(PactVerificationContext context) {
        context.verifyInteraction();
    }
    
    @State("state")
    public void setupState() {
        // Setup
    }
}
```

---

**💡 Tip**: Keep this reference handy while developing tests!

