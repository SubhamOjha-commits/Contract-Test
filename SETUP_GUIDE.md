# 🚀 Quick Setup Guide

## Step-by-Step Setup Instructions

### 1. Prerequisites Check

Verify you have all required tools installed:

```bash
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.9+)
mvn -version

# Check Docker version
docker --version
docker-compose --version

# Check Git
git --version
```

### 2. Clone and Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/pact-contract-testing.git
cd pact-contract-testing

# Copy environment file
cp .env.example .env
```

### 3. Start Pact Broker

```bash
# Start Pact Broker and PostgreSQL
docker-compose up -d

# Wait for services to be ready (takes ~30 seconds)
sleep 30

# Verify Pact Broker is running
curl http://localhost:9292/diagnostic/status/heartbeat

# Expected output: {"status":"ok"}
```

### 4. Build the Project

```bash
# Build parent POM and all modules
mvn clean install -DskipTests

# Verify build success
echo "✅ Build completed successfully!"
```

### 5. Run Consumer Tests

```bash
# Navigate to consumer service
cd consumer-service

# Run consumer contract tests
mvn clean test

# Check generated PACT files
ls -la target/pacts/

# Expected output:
# - UserConsumer-UserProvider.json
# - ProductConsumer-ProductProvider.json
```

### 6. Run Provider Verification

```bash
# Navigate to provider service
cd ../provider-service

# Run provider verification tests
mvn clean test

# Verify all tests passed
echo "✅ Provider verification completed!"
```

### 7. Access Pact Broker UI

Open your browser and navigate to:
- **URL**: http://localhost:9292
- **Dashboard**: View all contracts and compatibility matrix
- **HAL Browser**: http://localhost:9292/hal-browser/browser.html

### 8. Publish Contracts (Optional)

```bash
# Publish consumer contracts to broker
cd consumer-service
mvn pact:publish \
  -Dpact.broker.url=http://localhost:9292 \
  -Dpact.consumer.version=1.0.0 \
  -Dpact.tag=main

# Verify in Pact Broker UI
```

## 🎯 Quick Test Commands

### Run All Tests
```bash
# From project root
mvn clean test
```

### Run Only Consumer Tests
```bash
cd consumer-service
mvn test -Dtest=*ConsumerPactTest
```

### Run Only Provider Tests
```bash
cd provider-service
mvn test -Dtest=*ProviderPactTest
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserConsumerPactTest
```

## 🔍 Verification Checklist

- [ ] Java 17+ installed
- [ ] Maven 3.9+ installed
- [ ] Docker and Docker Compose installed
- [ ] Pact Broker running on port 9292
- [ ] Consumer tests generate PACT files
- [ ] Provider tests verify contracts
- [ ] Pact Broker UI accessible
- [ ] All tests passing

## 🐛 Common Setup Issues

### Issue 1: Port 9292 Already in Use

```bash
# Find process using port 9292
lsof -i :9292

# Kill the process
kill -9 <PID>

# Or change port in docker-compose.yml
```

### Issue 2: Docker Compose Fails

```bash
# Stop all containers
docker-compose down

# Remove volumes
docker-compose down -v

# Restart
docker-compose up -d
```

### Issue 3: Maven Build Fails

```bash
# Clear Maven cache
rm -rf ~/.m2/repository/au/com/dius/pact

# Rebuild
mvn clean install -U
```

### Issue 4: Tests Can't Find PACT Files

```bash
# Ensure consumer tests run first
cd consumer-service
mvn clean test

# Then run provider tests
cd ../provider-service
mvn clean test
```

## 📝 Next Steps

1. ✅ Explore the test code in `consumer-service/src/test/java`
2. ✅ Review provider verification in `provider-service/src/test/java`
3. ✅ Check Pact Broker UI for contract visualization
4. ✅ Try modifying a contract and see verification fail
5. ✅ Set up GitHub Actions (if using GitHub)

## 🎓 Learning Path

1. **Understand Consumer Tests**: Start with `UserConsumerPactTest.java`
2. **Understand Provider Tests**: Review `UserProviderPactTest.java`
3. **Explore Pact Broker**: Navigate the UI and understand the matrix
4. **Modify Contracts**: Try breaking changes and see what happens
5. **CI/CD Integration**: Set up GitHub Actions workflows

## 📚 Additional Resources

- [PACT Documentation](https://docs.pact.io/)
- [REST Assured Guide](https://rest-assured.io/)
- [Contract Testing Best Practices](https://docs.pact.io/getting_started/how_pact_works)

---

**Need Help?** Open an issue on GitHub or check the troubleshooting section in README.md

