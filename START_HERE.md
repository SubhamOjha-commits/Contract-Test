# 🚀 START HERE - Quick Setup Guide

## ⚠️ Prerequisites Not Installed

Your system is missing **Maven** and **Docker**, which are required to run this project.

## 🔧 Quick Installation (5 minutes)

### Option 1: Automated Installation (Recommended)

Run the installation script I've created for you:

```bash
./install-prerequisites.sh
```

This will automatically install:
- ✅ Homebrew (if not installed)
- ✅ Maven
- ✅ Docker Desktop

### Option 2: Manual Installation

#### Install Maven
```bash
# Install Homebrew first (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Maven
brew install maven

# Verify
mvn -version
```

#### Install Docker Desktop
```bash
# Option A: Using Homebrew
brew install --cask docker

# Option B: Download manually
# Visit: https://www.docker.com/products/docker-desktop
# Download and install Docker Desktop for Mac
```

## 🎯 After Installation

Once Maven and Docker are installed:

### Step 1: Build the Project
```bash
mvn clean install -DskipTests
```

### Step 2: Start Pact Broker
```bash
# Start Docker Desktop first (if not running)
# Then run:
docker-compose up -d

# Wait 30 seconds for Pact Broker to start
sleep 30

# Verify it's running
curl http://localhost:9292/diagnostic/status/heartbeat
```

### Step 3: Run Consumer Tests
```bash
cd consumer-service
mvn clean test
```

Expected output:
```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Step 4: Run Provider Tests
```bash
cd ../provider-service
mvn clean test
```

Expected output:
```
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Step 5: View Pact Broker
```bash
# Open in browser
open http://localhost:9292
```

## 📚 Using Make Commands (After Installation)

Once Maven and Docker are installed, you can use convenient make commands:

```bash
# See all available commands
make help

# Quick start (setup + start broker + run tests)
make quickstart

# Run consumer tests
make consumer-test

# Run provider tests
make provider-test

# Run all tests
make test

# Stop Pact Broker
make stop-broker
```

## 🎓 What You'll See

### Consumer Tests Output
```
[INFO] Running com.sdet.pact.consumer.contracts.UserConsumerPactTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

Generated PACT files:
✅ consumer-service/target/pacts/UserConsumer-UserProvider.json
✅ consumer-service/target/pacts/ProductConsumer-ProductProvider.json
```

### Provider Tests Output
```
[INFO] Running com.sdet.pact.provider.verification.UserProviderPactTest
[INFO] Verifying contracts from: ../consumer-service/target/pacts
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

✅ All provider verifications passed!
```

### Pact Broker UI
- Dashboard showing all contracts
- Compatibility matrix
- Version history
- Network graph

## 🐛 Troubleshooting

### "mvn: command not found"
**Solution**: Maven is not installed. Run `./install-prerequisites.sh`

### "docker: command not found"
**Solution**: Docker is not installed. Run `./install-prerequisites.sh`

### "Cannot connect to Docker daemon"
**Solution**: Start Docker Desktop application

### Port 9292 already in use
```bash
# Find and kill the process
lsof -i :9292
kill -9 <PID>
```

### Tests fail with "Connection refused"
**Solution**: Make sure Pact Broker is running
```bash
docker-compose up -d
sleep 30
curl http://localhost:9292/diagnostic/status/heartbeat
```

## 📖 Documentation Guide

After installation, explore these documents in order:

1. **START_HERE.md** ← You are here
2. **INSTALLATION_GUIDE.md** - Detailed installation instructions
3. **SETUP_GUIDE.md** - Complete setup walkthrough
4. **QUICK_REFERENCE.md** - Command cheat sheet
5. **EXAMPLES.md** - Code examples
6. **INTERVIEW_GUIDE.md** - Interview preparation
7. **PROJECT_SUMMARY.md** - Project overview

## ✅ Verification Checklist

Before running tests, verify:

- [ ] Java is installed (`java -version`)
- [ ] Maven is installed (`mvn -version`)
- [ ] Docker is installed (`docker --version`)
- [ ] Docker Desktop is running
- [ ] You're in the project directory (`/Users/subhamo/DemoProject`)

## 🎯 Quick Commands Summary

```bash
# Install prerequisites
./install-prerequisites.sh

# Build project
mvn clean install -DskipTests

# Start Pact Broker
docker-compose up -d

# Run consumer tests
cd consumer-service && mvn test

# Run provider tests
cd ../provider-service && mvn test

# View Pact Broker
open http://localhost:9292

# Stop everything
docker-compose down
```

## 🚀 Ready to Go?

1. Run: `./install-prerequisites.sh`
2. Wait for installation to complete
3. Follow the "After Installation" steps above
4. Enjoy your PACT Contract Testing Framework!

---

**Questions?** Check INSTALLATION_GUIDE.md or SETUP_GUIDE.md for detailed help.

**Good luck! 🎉**

