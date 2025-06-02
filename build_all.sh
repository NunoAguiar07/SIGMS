#!/bin/bash

set -e  # Exit on first error

# Function to check if a command exists
check_command() {
    if ! command -v "$1" &> /dev/null; then
        echo "❌ Required command '$1' is not installed or not in PATH."
        exit 1
    fi
}

# Check Java (either java command OR JAVA_HOME must be set)
if ! command -v java &> /dev/null && [ -z "$JAVA_HOME" ]; then
    echo "❌ Neither 'java' command is available nor is JAVA_HOME set."
    exit 1
fi

# Check Node.js, npm, npx
check_command node
check_command npm
check_command npx

# Check Go
check_command go

echo "✅ Environment checks passed."
echo ""

# Ask if the user wants to build the hardware component
read -p "🛠️  Do you want to build the hardware component? (y/n): " build_hardware
build_hardware=$(echo "$build_hardware" | tr '[:upper:]' '[:lower:]')  # Normalize input

# Build order
DIRS=("backend" "frontend")
if [[ "$build_hardware" == "y" || "$build_hardware" == "yes" ]]; then
    DIRS+=("hardware")
fi

# Build each component
for dir in "${DIRS[@]}"; do
    echo "📦 Building $dir..."
    if [ -x "$dir/build.bat" ]; then
        (cd "$dir" && ./build.bat)
        echo "✅ $dir built successfully."
    else
        echo "❌ $dir/build.bat not found or not executable."
        exit 1
    fi
    echo ""
done

echo "🎉 All components built successfully!"
