#!/bin/bash

set -e

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path-to-img-file>"
    exit 1
fi

IMG_FILE="$1"
MOUNT_BOOT="/mnt/rpi-boot"
MOUNT_ROOT="/mnt/rpi-root"

# Create mount points
sudo mkdir -p "$MOUNT_BOOT" "$MOUNT_ROOT"

# Attach the image and get loop device
LOOP_DEVICE=$(sudo losetup --find --partscan --show "$IMG_FILE")

# Ensure partitions exist
if ! [ -e "${LOOP_DEVICE}p2" ]; then
    echo "Partitioned loop device not found. Trying kpartx..."
    sudo kpartx -av "$LOOP_DEVICE"
    ROOT_PART="/dev/mapper/$(basename ${LOOP_DEVICE})p2"
    BOOT_PART="/dev/mapper/$(basename ${LOOP_DEVICE})p1"
else
    ROOT_PART="${LOOP_DEVICE}p2"
    BOOT_PART="${LOOP_DEVICE}p1"
fi

# Mount the partitions
sudo mount "$ROOT_PART" "$MOUNT_ROOT"
sudo mount "$BOOT_PART" "$MOUNT_BOOT"

# Copy the script
echo "[*] Copying start.sh to /home/pi/"
sudo cp start.sh "$MOUNT_ROOT/home/pi/start.sh"
sudo cp hardware "$MOUNT_ROOT/home/pi/hardware"
sudo chmod +x "$MOUNT_ROOT/home/pi/start.sh"
sudo chmod +x "$MOUNT_ROOT/home/pi/hardware"

RC_LOCAL="/mnt/rpi-root/etc/rc.local"

if [ ! -f "$RC_LOCAL" ]; then
  echo "Creating $RC_LOCAL with minimal content..."
  sudo tee "$RC_LOCAL" > /dev/null << 'EOF'
#!/bin/sh -e
exit 0
EOF
  sudo chmod +x "$RC_LOCAL"
fi

sudo sed -i "/^exit 0/i bash /home/pi/start.sh &" "$RC_LOCAL"

echo "[*] Unmounting..."
sudo umount "$MOUNT_ROOT"
sudo umount "$MOUNT_BOOT"

if [[ "$ROOT_PART" == /dev/mapper/* ]]; then
    sudo kpartx -d "$LOOP_DEVICE"
fi

sudo losetup -d "$LOOP_DEVICE"

echo "[✓] Done. Your image is ready."