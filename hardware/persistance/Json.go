package persistance

import (
	"encoding/json"
	"fmt"
	"hardware/packetsniffer"
	"os"
)

type Device struct {
	Ewma         packetsniffer.EWMA `json:"ewma"`
	Id           string             `json:"id"`
	RoomId       int                `json:"roomId"`
	RoomCapacity int                `json:"roomCapacity"`
}

func (dev *Device) Save() error {
	jsonData, err := json.MarshalIndent(dev, "", "  ")
	if err != nil {
		return fmt.Errorf("failed to marshal struct: %w", err)
	}
	err = os.WriteFile("device.json", jsonData, 0644)
	if err != nil {
		return fmt.Errorf("failed to write file: %w", err)
	}
	return nil
}

func Load() (*Device, error) {
	jsonFile, err := os.ReadFile("device.json")
	if err != nil {
		if os.IsNotExist(err) {
			return &Device{RoomCapacity: 30}, nil
		}
		return nil, fmt.Errorf("failed to open file: %w", err)
	}
	var device Device
	err = json.Unmarshal(jsonFile, &device)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal json: %w", err)
	}
	return &device, nil
}
