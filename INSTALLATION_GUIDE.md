# 🔧 Installation Guide

## Prerequisites Installation

This guide will help you install all required tools to run the PACT Contract Testing Framework.

## 1. Java Installation

### Check if Java is installed
```bash
java -version
```

✅ **You already have Java 25 installed!**

## 2. Maven Installation

### macOS Installation Options

#### Option A: Using Homebrew (Recommended)

1. **Install Homebrew** (if not already installed):
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

2. **Install Maven**:
```bash
brew install maven
```

3. **Verify installation**:
```bash
mvn -version
```

Expected output:
```
Apache Maven 3.9.x
Maven home: /opt/homebrew/Cellar/maven/...
Java version: 25.0.1
```

#### Option B: Manual Installation

1. **Download Maven**:
```bash
cd ~/Downloads
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
```

2. **Extract and move**:
```bash
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
```

3. **Add to PATH**:
```bash
# For zsh (default on macOS)
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# For bash
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bash_profile
source ~/.bash_profile
```

4. **Verify**:
```bash
mvn -version
```

#### Option C: Using SDKMAN (Alternative)

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Maven
sdk install maven

# Verify
mvn -version
```

## 3. Docker Installation

### Install Docker Desktop for Mac

1. **Download Docker Desktop**:
   - Visit: https://www.docker.com/products/docker-desktop
   - Download for macOS (Apple Silicon or Intel)

2. **Install**:
   - Open the downloaded .dmg file
   - Drag Docker to Applications
   - Launch Docker Desktop

3. **Verify**:
```bash
docker --version
docker-compose --version
```

### Alternative: Using Homebrew

```bash
brew install --cask docker
```

## 4. Git (Usually pre-installed on macOS)

```bash
# Verify Git installation
git --version

# If not installed
brew install git
```

## 5. Verify All Installations

Run this verification script:

```bash
echo "=== Checking Prerequisites ==="
echo ""

echo "Java:"
java -version
echo ""

echo "Maven:"
mvn -version
echo ""

echo "Docker:"
docker --version
echo ""

echo "Docker Compose:"
docker-compose --version
echo ""

echo "Git:"
git --version
echo ""

echo "=== All checks complete ==="
```

## 6. Post-Installation Steps

### After Maven is installed:

1. **Build the project**:
```bash
cd /Users/subhamo/DemoProject
mvn clean install -DskipTests
```

2. **Start Pact Broker**:
```bash
docker-compose up -d
```

3. **Run Consumer Tests**:
```bash
cd consumer-service
mvn clean test
```

4. **Run Provider Tests**:
```bash
cd ../provider-service
mvn clean test
```

## Troubleshooting

### Maven command not found after installation

**Solution**: Reload your shell configuration
```bash
source ~/.zshrc  # for zsh
# OR
source ~/.bash_profile  # for bash
```

### Docker daemon not running

**Solution**: 
1. Open Docker Desktop application
2. Wait for it to start (whale icon in menu bar)
3. Try docker commands again

### Permission denied errors

**Solution**: Add your user to docker group
```bash
sudo usermod -aG docker $USER
```

### Port 9292 already in use

**Solution**: Find and kill the process
```bash
lsof -i :9292
kill -9 <PID>
```

## Quick Installation Script

Save this as `install.sh` and run it:

```bash
#!/bin/bash

echo "🚀 Installing prerequisites for PACT Contract Testing Framework"

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "📦 Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
fi

# Install Maven
if ! command -v mvn &> /dev/null; then
    echo "📦 Installing Maven..."
    brew install maven
else
    echo "✅ Maven already installed"
fi

# Install Docker (if not installed)
if ! command -v docker &> /dev/null; then
    echo "📦 Installing Docker..."
    brew install --cask docker
    echo "⚠️  Please start Docker Desktop manually"
else
    echo "✅ Docker already installed"
fi

echo ""
echo "✅ Installation complete!"
echo ""
echo "Next steps:"
echo "1. Start Docker Desktop"
echo "2. Run: cd /Users/subhamo/DemoProject"
echo "3. Run: mvn clean install -DskipTests"
echo "4. Run: docker-compose up -d"
echo "5. Run: make consumer-test"
```

## Next Steps

Once all tools are installed:

1. ✅ Follow [SETUP_GUIDE.md](SETUP_GUIDE.md) for project setup
2. ✅ Review [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for commands
3. ✅ Start testing with [EXAMPLES.md](EXAMPLES.md)

---

**Need Help?** Check the troubleshooting section or open an issue on GitHub.

