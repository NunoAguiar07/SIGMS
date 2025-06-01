#!/bin/bash

# -----------------------------
# Export Web Build via Expo
# -----------------------------

# Exit immediately if a command exits with a non-zero status
set -e

# Optional: go to your project directory
# cd /path/to/your/expo/project

echo "🌐 Starting Expo Web export..."

# Run Expo export for web
npx expo export --platform web

# Check if the export was successful
if [ $? -eq 0 ]; then
  echo "✅ Web export completed successfully!"
else
  echo "❌ Export failed. Please check the logs above."
  exit 1
fi