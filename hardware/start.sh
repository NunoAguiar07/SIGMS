#!/bin/bash

set -e

echo "[*] Updating package list..."
sudo apt update

echo "[*] Upgrading existing packages..."
sudo apt full-upgrade -y

if ! dpkg -s wireshark >/dev/null 2>&1; then
    echo "[*] Wireshark not found. Installing..."
    sudo apt install -y wireshark

    echo "[?] Do you want to allow non-root users to capture packets?"
    sudo dpkg-reconfigure wireshark-common
else
    echo "[✓] Wireshark is already installed. Skipping installation."
fi

echo "[*] Adding current user ($USER) to 'wireshark' group..."
sudo usermod -aG wireshark "$USER"

HARDWARE_PATH="/home/pi/hardware"
if [ -x "$HARDWARE_PATH" ]; then
    echo "[*] Running $HARDWARE_PATH..."
    "$HARDWARE_PATH"
else
    echo "[!] Cannot run $HARDWARE_PATH: file not found or not executable."
    exit 1
fi