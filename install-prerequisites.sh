#!/bin/bash

# PACT Contract Testing Framework - Prerequisites Installation Script
# This script installs all required tools for the project

set -e  # Exit on error

echo "🚀 PACT Contract Testing Framework - Prerequisites Installation"
echo "================================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Check if running on macOS
if [[ "$OSTYPE" != "darwin"* ]]; then
    print_error "This script is designed for macOS. Please install prerequisites manually."
    exit 1
fi

print_info "Detected macOS system"
echo ""

# 1. Check Java
echo "1️⃣  Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    print_success "Java is already installed: $JAVA_VERSION"
else
    print_error "Java is not installed"
    print_info "Please install Java 17 or higher from: https://adoptium.net/"
    exit 1
fi
echo ""

# 2. Check and Install Homebrew
echo "2️⃣  Checking Homebrew installation..."
if command -v brew &> /dev/null; then
    print_success "Homebrew is already installed"
else
    print_info "Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    
    # Add Homebrew to PATH for Apple Silicon Macs
    if [[ $(uname -m) == 'arm64' ]]; then
        echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
        eval "$(/opt/homebrew/bin/brew shellenv)"
    fi
    
    print_success "Homebrew installed successfully"
fi
echo ""

# 3. Check and Install Maven
echo "3️⃣  Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    print_success "Maven is already installed: $MVN_VERSION"
else
    print_info "Installing Maven..."
    brew install maven
    print_success "Maven installed successfully"
fi
echo ""

# 4. Check and Install Docker
echo "4️⃣  Checking Docker installation..."
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_success "Docker is already installed: $DOCKER_VERSION"
else
    print_info "Installing Docker Desktop..."
    brew install --cask docker
    print_success "Docker Desktop installed"
    print_info "⚠️  Please start Docker Desktop manually from Applications"
    print_info "⚠️  Wait for Docker to start before running tests"
fi
echo ""

# 5. Verify installations
echo "5️⃣  Verifying all installations..."
echo ""

echo "Java:"
java -version 2>&1 | head -n 1
echo ""

echo "Maven:"
mvn -version | head -n 1
echo ""

echo "Docker:"
if command -v docker &> /dev/null; then
    docker --version
else
    print_info "Docker installed but not running. Start Docker Desktop."
fi
echo ""

echo "Git:"
git --version
echo ""

# 6. Summary
echo "================================================================"
echo "📋 Installation Summary"
echo "================================================================"
print_success "All prerequisites are installed!"
echo ""
echo "Next steps:"
echo "  1. If Docker was just installed, start Docker Desktop"
echo "  2. Run: cd /Users/subhamo/DemoProject"
echo "  3. Run: make setup"
echo "  4. Run: make start-broker"
echo "  5. Run: make consumer-test"
echo ""
echo "For detailed instructions, see SETUP_GUIDE.md"
echo "================================================================"

