#!/bin/bash

set -e

echo "[*] Looking up the latest Raspberry Pi OS Lite (32-bit, armhf)..."

BASE_URL="https://downloads.raspberrypi.com/raspios_lite_armhf/images/"
LATEST_PATH=$(curl -s $BASE_URL | grep -Eo 'raspios_lite_armhf-[^/"]+' | sort | tail -n 1)

if [ -z "$LATEST_PATH" ]; then
    echo "[!] Could not find the latest image path."
    exit 1
fi

IMAGE_INDEX_URL="${BASE_URL}${LATEST_PATH}/"
IMAGE_URL=$(curl -s "$IMAGE_INDEX_URL" | grep -Eo 'href="[^"]+\.img\.xz"' | cut -d'"' -f2 | sort | tail -n 1)

if [ -z "$IMAGE_URL" ]; then
    echo "[!] Could not find an image file in $IMAGE_INDEX_URL"
    exit 1
fi

FULL_URL="${IMAGE_INDEX_URL}${IMAGE_URL}"
FILENAME_XZ=$(basename "$FULL_URL")
FILENAME_IMG="${FILENAME_XZ%.xz}"

echo "[*] Found latest image:"
echo "    $FULL_URL"

if [ -f "$FILENAME_IMG" ]; then
    echo "[*] Skipping extraction; $FILENAME_IMG already exists."
else
    echo "[*] Downloading $FILENAME_XZ..."
    curl -LO "$FULL_URL"
    echo "[*] Extracting $FILENAME_XZ..."
    unxz "$FILENAME_XZ"
fi

echo "[✓] Done. Extracted: $FILENAME_IMG"

echo "[*] Running generate_raspberry_image.sh with parameter $FILENAME_IMG..."
./generate_raspberry_image.sh "$FILENAME_IMG"
