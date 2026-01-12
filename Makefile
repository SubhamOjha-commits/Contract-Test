# PACT Contract Testing Framework - Makefile

.PHONY: help setup start-broker stop-broker build test consumer-test provider-test publish clean verify-all

# Default target
help:
	@echo "🔄 PACT Contract Testing Framework"
	@echo ""
	@echo "Available commands:"
	@echo "  make setup           - Initial project setup"
	@echo "  make start-broker    - Start Pact Broker with Docker"
	@echo "  make stop-broker     - Stop Pact Broker"
	@echo "  make build           - Build the project"
	@echo "  make test            - Run all tests"
	@echo "  make consumer-test   - Run consumer contract tests"
	@echo "  make provider-test   - Run provider verification tests"
	@echo "  make publish         - Publish contracts to broker"
	@echo "  make verify-all      - Run full verification pipeline"
	@echo "  make clean           - Clean build artifacts"
	@echo "  make status          - Check Pact Broker status"

# Initial setup
setup:
	@echo "🚀 Setting up PACT Contract Testing Framework..."
	@cp -n .env.example .env || true
	@echo "✅ Environment file created"
	@mvn clean install -DskipTests
	@echo "✅ Project built successfully"
	@echo "📝 Next step: Run 'make start-broker' to start Pact Broker"

# Start Pact Broker
start-broker:
	@echo "🐳 Starting Pact Broker..."
	@docker-compose up -d
	@echo "⏳ Waiting for Pact Broker to be ready..."
	@sleep 10
	@curl -f http://localhost:9292/diagnostic/status/heartbeat || (echo "❌ Pact Broker failed to start" && exit 1)
	@echo "✅ Pact Broker is running at http://localhost:9292"

# Stop Pact Broker
stop-broker:
	@echo "🛑 Stopping Pact Broker..."
	@docker-compose down
	@echo "✅ Pact Broker stopped"

# Build project
build:
	@echo "🔨 Building project..."
	@mvn clean install -DskipTests
	@echo "✅ Build completed"

# Run all tests
test:
	@echo "🧪 Running all tests..."
	@mvn clean test
	@echo "✅ All tests completed"

# Run consumer tests
consumer-test:
	@echo "📝 Running consumer contract tests..."
	@cd consumer-service && mvn clean test
	@echo "✅ Consumer tests completed"
	@echo "📄 PACT files generated in consumer-service/target/pacts/"

# Run provider tests
provider-test:
	@echo "🔍 Running provider verification tests..."
	@cd provider-service && mvn clean test
	@echo "✅ Provider verification completed"

# Publish contracts to broker
publish:
	@echo "📤 Publishing contracts to Pact Broker..."
	@cd consumer-service && mvn pact:publish \
		-Dpact.broker.url=http://localhost:9292 \
		-Dpact.consumer.version=1.0.0 \
		-Dpact.tag=main
	@echo "✅ Contracts published to Pact Broker"
	@echo "🔗 View at http://localhost:9292"

# Full verification pipeline
verify-all: consumer-test provider-test
	@echo "✅ Full verification pipeline completed successfully!"

# Clean build artifacts
clean:
	@echo "🧹 Cleaning build artifacts..."
	@mvn clean
	@rm -rf consumer-service/target
	@rm -rf provider-service/target
	@echo "✅ Clean completed"

# Check Pact Broker status
status:
	@echo "🔍 Checking Pact Broker status..."
	@curl -s http://localhost:9292/diagnostic/status/heartbeat | jq . || echo "❌ Pact Broker is not running"

# Quick start (setup + start broker + run tests)
quickstart: setup start-broker consumer-test provider-test
	@echo "🎉 Quick start completed!"
	@echo "🔗 Pact Broker: http://localhost:9292"

# Run tests with coverage
test-coverage:
	@echo "📊 Running tests with coverage..."
	@mvn clean test jacoco:report
	@echo "✅ Coverage report generated"

# View Pact Broker logs
logs:
	@docker-compose logs -f pact-broker

# Restart Pact Broker
restart-broker: stop-broker start-broker
	@echo "✅ Pact Broker restarted"

# Install dependencies
install:
	@echo "📦 Installing dependencies..."
	@mvn dependency:resolve
	@echo "✅ Dependencies installed"

# Format code
format:
	@echo "💅 Formatting code..."
	@mvn spotless:apply || echo "Spotless not configured"
	@echo "✅ Code formatted"

# Run specific test
test-class:
	@read -p "Enter test class name: " class; \
	mvn test -Dtest=$$class

# Generate project documentation
docs:
	@echo "📚 Generating documentation..."
	@mvn javadoc:javadoc
	@echo "✅ Documentation generated in target/site/apidocs"

