#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Ensure Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed or not in your PATH."
    exit 1
fi

# Ensure Docker Compose is available (either as plugin or legacy)
if ! docker compose version &> /dev/null; then
    echo "Docker Compose is not available. Please install it (as a plugin or legacy binary)."
    exit 1
fi

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Run the build_all.sh script
echo "Running build_all.sh..."
bash "$SCRIPT_DIR/build_all.sh"

# Change to the docker directory
cd "$SCRIPT_DIR/docker"

# Run Docker Compose build and up
echo "Running docker compose build..."
docker compose build

echo "Running docker compose up..."
docker compose up -d
