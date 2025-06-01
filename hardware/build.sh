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

echo "⬇️ Running download_latest_raspberrypi_lite.sh..."
./download_latest_raspberrypi_lite.sh