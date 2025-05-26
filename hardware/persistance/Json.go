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
	RoomId       string             `json:"roomId"`
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
