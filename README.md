# 🔄 PACT Contract Testing Framework

[![Consumer Tests](https://github.com/yourusername/pact-contract-testing/actions/workflows/consumer-contract-tests.yml/badge.svg)](https://github.com/yourusername/pact-contract-testing/actions/workflows/consumer-contract-tests.yml)
[![Provider Verification](https://github.com/yourusername/pact-contract-testing/actions/workflows/provider-verification-tests.yml/badge.svg)](https://github.com/yourusername/pact-contract-testing/actions/workflows/provider-verification-tests.yml)

> **Consumer-Driven Contract Testing Framework** using PACT and REST Assured for ensuring backward compatibility between microservices.

## 📋 Table of Contents

- [Overview](#overview)
- [Why Contract Testing?](#why-contract-testing)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [CI/CD Pipeline](#cicd-pipeline)
- [Pact Broker](#pact-broker)
- [Key Features](#key-features)
- [Best Practices](#best-practices)

## 🎯 Overview

This project demonstrates a **production-ready Contract Testing framework** that validates API contracts between microservices independently of service deployment. It ensures that changes to provider APIs don't break consumer expectations.

### Problem Statement

In microservices architecture:
- **Traditional Integration Tests** require all services to be running
- **API changes** can break consumers without warning
- **Deployment dependencies** slow down release cycles
- **Version compatibility** is hard to track

### Solution

**Consumer-Driven Contract Testing** with PACT:
- ✅ Consumers define expected API behavior
- ✅ Contracts are verified independently
- ✅ No need for running services during testing
- ✅ Backward compatibility guaranteed
- ✅ Safe, independent deployments

## 🏗️ Architecture

```
┌─────────────────┐         ┌──────────────┐         ┌─────────────────┐
│                 │         │              │         │                 │
│   Consumer      │────────▶│  PACT File   │────────▶│   Provider      │
│   Service       │ Creates │  (Contract)  │ Verifies│   Service       │
│                 │         │              │         │                 │
└─────────────────┘         └──────────────┘         └─────────────────┘
        │                          │                          │
        │                          ▼                          │
        │                   ┌──────────────┐                 │
        └──────────────────▶│ Pact Broker  │◀────────────────┘
                            │  (Versioned) │
                            └──────────────┘
```

### Workflow

1. **Consumer** defines expectations and generates PACT files
2. **PACT files** are published to Pact Broker
3. **Provider** verifies it satisfies all consumer contracts
4. **Compatibility matrix** determines safe deployments

## 🛠️ Tech Stack

| Component            | Technology                    | Version |
|---------------------|-------------------------------|---------|
| Language            | Java                          | 17      |
| Build Tool          | Maven                         | 3.9+    |
| Contract Testing    | PACT JVM                      | 4.6.5   |
| API Testing         | REST Assured                  | 5.4.0   |
| Testing Framework   | JUnit 5                       | 5.10.1  |
| Contract Storage    | Pact Broker                   | Latest  |
| Database            | PostgreSQL                    | 15      |
| Containerization    | Docker & Docker Compose       | -       |
| CI/CD               | GitHub Actions                | -       |
| Reporting           | Allure (optional)             | 2.25.0  |

## 📁 Project Structure

```
pact-contract-testing/
│
├── consumer-service/                    # Consumer contract tests
│   ├── src/test/java/
│   │   └── com/sdet/pact/consumer/
│   │       └── contracts/
│   │           ├── UserConsumerPactTest.java
│   │           └── ProductConsumerPactTest.java
│   ├── pom.xml
│   └── target/pacts/                    # Generated PACT files
│
├── provider-service/                    # Provider verification tests
│   ├── src/test/java/
│   │   └── com/sdet/pact/provider/
│   │       ├── verification/
│   │       │   ├── UserProviderPactTest.java
│   │       │   └── ProductProviderPactTest.java
│   │       └── stubs/
│   │           ├── UserProviderStub.java
│   │           └── ProductProviderStub.java
│   └── pom.xml
│
├── .github/workflows/                   # CI/CD pipelines
│   ├── consumer-contract-tests.yml
│   ├── provider-verification-tests.yml
│   └── pact-full-pipeline.yml
│
├── docker-compose.yml                   # Pact Broker setup
├── pom.xml                              # Parent POM
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pact-contract-testing.git
   cd pact-contract-testing
   ```

2. **Start Pact Broker**
   ```bash
   docker-compose up -d
   ```

3. **Verify Pact Broker is running**
   ```bash
   curl http://localhost:9292/diagnostic/status/heartbeat
   ```
   
   Access Pact Broker UI: http://localhost:9292

4. **Build the project**
   ```bash
   mvn clean install -DskipTests
   ```

## 🧪 Running Tests

### Consumer Contract Tests

Generate consumer contracts:

```bash
cd consumer-service
mvn clean test
```

This will:
- Run consumer tests against mock provider
- Generate PACT files in `target/pacts/`
- Validate consumer expectations

### Provider Verification Tests

Verify provider against contracts:

```bash
cd provider-service
mvn clean test
```

This will:
- Load PACT files from consumer
- Start provider stub service
- Verify all contract interactions
- Report verification results

### Publish Contracts to Broker

```bash
cd consumer-service
mvn pact:publish \
  -Dpact.broker.url=http://localhost:9292 \
  -Dpact.consumer.version=1.0.0
```

### Run Full Test Suite

```bash
mvn clean test
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

1. **Consumer Contract Tests** (`.github/workflows/consumer-contract-tests.yml`)
   - Runs on consumer code changes
   - Generates and publishes PACT files
   - Uploads contracts as artifacts

2. **Provider Verification Tests** (`.github/workflows/provider-verification-tests.yml`)
   - Runs on provider code changes
   - Verifies against published contracts
   - Publishes verification results

3. **Full Pipeline** (`.github/workflows/pact-full-pipeline.yml`)
   - Runs both consumer and provider tests
   - Generates compatibility report
   - Determines deployment safety

### Pipeline Flow

```
┌──────────────────┐
│  Code Push/PR    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Consumer Tests   │──────┐
│ Generate PACTs   │      │
└──────────────────┘      │
                          ▼
                   ┌──────────────────┐
                   │ Publish to       │
                   │ Pact Broker      │
                   └────────┬─────────┘
                            │
                            ▼
                   ┌──────────────────┐
                   │ Provider         │
                   │ Verification     │
                   └────────┬─────────┘
                            │
                            ▼
                   ┌──────────────────┐
                   │ Can-I-Deploy?    │
                   │ Check            │
                   └────────┬─────────┘
                            │
                            ▼
                   ┌──────────────────┐
                   │ ✅ Safe to       │
                   │    Deploy        │
                   └──────────────────┘
```

## 📊 Pact Broker

### Features

- **Contract Versioning**: Track all contract versions
- **Compatibility Matrix**: View consumer-provider compatibility
- **Webhooks**: Trigger provider verification on new contracts
- **Can-I-Deploy**: Check if it's safe to deploy
- **Network Graph**: Visualize service dependencies

### Accessing Pact Broker

**Local Development:**
- URL: http://localhost:9292
- No authentication required (configured for local use)

**Key Endpoints:**
- Dashboard: http://localhost:9292
- HAL Browser: http://localhost:9292/hal-browser/browser.html
- Health Check: http://localhost:9292/diagnostic/status/heartbeat

### Using Pact Broker CLI

```bash
# Check if consumer can be deployed
pact-broker can-i-deploy \
  --pacticipant UserConsumer \
  --version 1.0.0 \
  --to-environment production \
  --broker-base-url http://localhost:9292

# View compatibility matrix
pact-broker matrix \
  --pacticipant UserConsumer \
  --pacticipant UserProvider \
  --broker-base-url http://localhost:9292
```

## ✨ Key Features

### 1. Consumer-Driven Contracts

**Example: User Service Consumer Test**

```java
@Pact(consumer = "UserConsumer", provider = "UserProvider")
public V4Pact getUserByIdPact(PactDslWithProvider builder) {
    return builder
        .given("User with ID 1 exists")
        .uponReceiving("A request to get user by ID")
            .path("/api/users/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("name", "John Doe")
                .stringType("email", "john.doe@example.com")
            )
        .toPact(V4Pact.class);
}
```

### 2. Provider State Management

**Example: Provider State Handler**

```java
@State("User with ID 1 exists")
public void userWithId1Exists() {
    // Setup test data
    providerStub.addUser(1, "John Doe", "john.doe@example.com", "ADMIN", true);
}
```

### 3. REST Assured Integration

**Example: Consumer Test with REST Assured**

```java
@Test
@PactTestFor(pactMethod = "getUserByIdPact")
void testGetUserById(MockServer mockServer) {
    given()
        .baseUri(mockServer.getUrl())
        .contentType("application/json")
    .when()
        .get("/api/users/1")
    .then()
        .statusCode(200)
        .body("id", equalTo(1))
        .body("name", notNullValue());
}
```

### 4. Multiple Contract Scenarios

- ✅ **Success scenarios** (200 OK)
- ✅ **Not found scenarios** (404)
- ✅ **Create operations** (201 Created)
- ✅ **Update operations** (200 OK)
- ✅ **Delete operations** (204 No Content)
- ✅ **Query parameters**
- ✅ **Request/Response validation**

### 5. Automated CI/CD Integration

- Automatic contract generation
- Contract publishing to broker
- Provider verification on changes
- Deployment safety checks
- PR comments with test results

## 📚 Best Practices

### Consumer Side

1. **Define Clear Expectations**
   - Use type matchers instead of exact values
   - Focus on structure, not specific data
   - Keep contracts minimal and focused

2. **Use Provider States**
   - Define clear preconditions
   - Make states reusable
   - Document state requirements

3. **Version Your Contracts**
   - Tag contracts with version numbers
   - Use semantic versioning
   - Track breaking changes

### Provider Side

1. **Implement All States**
   - Handle all consumer-defined states
   - Use test data builders
   - Keep state setup isolated

2. **Verify Regularly**
   - Run verification on every change
   - Verify against all consumer versions
   - Don't break existing contracts

3. **Communicate Changes**
   - Notify consumers of breaking changes
   - Provide migration guides
   - Use deprecation warnings

### General

1. **Keep Contracts Simple**
   - One interaction per test
   - Clear, descriptive names
   - Minimal test data

2. **Use Pact Broker**
   - Centralized contract storage
   - Version tracking
   - Deployment safety checks

3. **Automate Everything**
   - CI/CD integration
   - Automatic verification
   - Deployment gates

## 🔧 Troubleshooting

### Common Issues

**1. Pact Broker not starting**
```bash
# Check Docker logs
docker-compose logs pact-broker

# Restart services
docker-compose down
docker-compose up -d
```

**2. Tests failing with "Connection refused"**
- Ensure Pact Broker is running
- Check port 9292 is not in use
- Verify Docker network connectivity

**3. Provider verification fails**
- Check provider state handlers are implemented
- Verify PACT files are in correct location
- Ensure provider stub is running on correct port

**4. Maven build issues**
```bash
# Clean and rebuild
mvn clean install -U

# Clear Maven cache
rm -rf ~/.m2/repository/au/com/dius/pact
```

## 📈 Advanced Features (Future Enhancements)

### 1. Negative Contract Testing
- Invalid request formats
- Missing required fields
- Unauthorized access scenarios

### 2. Schema Evolution
- Optional fields handling
- Deprecated fields
- Version negotiation

### 3. Async Contract Testing
- Kafka message contracts
- Event-driven architectures

### 4. Multiple Consumers
- Different consumer requirements
- Version compatibility matrix

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.


## 🙏 Acknowledgments

- [Pact Foundation](https://pact.io/) for the contract testing framework
- [REST Assured](https://rest-assured.io/) for API testing
- Community contributors

---


