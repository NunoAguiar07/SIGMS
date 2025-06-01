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

# List of directories with build.sh
DIRS=("backend" "frontend" "hardware")

# Build each component
for dir in "${DIRS[@]}"; do
    echo "📦 Building $dir..."
    if [ -x "$dir/build.sh" ]; then
        (cd "$dir" && ./build.sh)
        echo "✅ $dir built successfully."
    else
        echo "❌ $dir/build.sh not found or not executable."
        exit 1
    fi
    echo ""
done

echo "🎉 All components built successfully!"
