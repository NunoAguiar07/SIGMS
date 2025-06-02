#!/bin/bash

OUTPUT="hardware"

echo "🛠️ Building Go project..."

go build -o "$OUTPUT"

if [ $? -eq 0 ]; then
    echo "✅ Build successful: ./$OUTPUT"
else
    echo "❌ Build failed."
    exit 1
fi

# Prompt to generate Raspberry Pi OS image
read -p "🍓 Do you want to generate the Raspberry Pi OS image? (y/n): " generate_image
generate_image=$(echo "$generate_image" | tr '[:upper:]' '[:lower:]')

if [[ "$generate_image" == "y" || "$generate_image" == "yes" ]]; then
    echo "⬇️ Running download_latest_raspberrypi_lite.sh..."
    ./download_latest_raspberrypi_lite.sh
else
    echo "⏭️ Skipping Raspberry Pi OS image generation."
fi