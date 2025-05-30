#!/bin/bash

# Gradle project build script
# Usage: ./build.sh [clean|test|jar]

set -e  # Exit on any error

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "Building Gradle project in: $PROJECT_DIR"

# Function to check if gradlew exists
check_gradle_wrapper() {
    if [[ -f "./gradlew" ]]; then
        GRADLE_CMD="./gradlew"
        chmod +x ./gradlew
    elif command -v gradle &> /dev/null; then
        GRADLE_CMD="gradle"
    else
        echo "Error: Neither ./gradlew nor gradle command found!"
        exit 1
    fi
}

# Function to build the project
build_project() {
    echo "Compiling project..."
    $GRADLE_CMD build
    echo "Build completed successfully!"
}

# Function to clean the project
clean_project() {
    echo "Cleaning project..."
    $GRADLE_CMD clean
    echo "Clean completed!"
}

# Function to run tests
test_project() {
    echo "Running tests..."
    $GRADLE_CMD test
    echo "Tests completed!"
}

# Function to create JAR
jar_project() {
    echo "Creating JAR..."
    $GRADLE_CMD jar
    echo "JAR created successfully!"
}

# Main execution
check_gradle_wrapper

case "${1:-build}" in
    "clean")
        clean_project
        ;;
    "test")
        test_project
        ;;
    "jar")
        jar_project
        ;;
    "build")
        build_project
        ;;
    *)
        echo "Usage: $0 [clean|test|jar|build]"
        echo "  clean - Clean the project"
        echo "  test  - Run tests"
        echo "  jar   - Create JAR file"
        echo "  build - Compile project (default)"
        exit 1
        ;;
esac