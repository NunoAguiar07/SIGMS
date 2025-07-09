package persistance

import (
	"hardware/packetsniffer"
	"testing"
)

func TestDevice_SaveAndLoad(t *testing.T) {
	ewma := packetsniffer.NewEWMA(0.1)
	device := Device{*ewma, "aRandomId", 12, 30}
	err := device.Save()
	if err != nil {
		t.Error(err)
	}
	loadedDevice, err := Load()
	if err != nil {
		t.Error(err)
	}
	if loadedDevice.Id != device.Id {
		t.Error("Device Id mismatch")
	}
	if loadedDevice.RoomId != device.RoomId {
		t.Error("Device RoomId mismatch")
	}
	if loadedDevice.RoomCapacity != device.RoomCapacity {
		t.Error("Device RoomCapacity mismatch")
	}
}
