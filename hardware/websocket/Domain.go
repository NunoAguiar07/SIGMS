package websocket

import (
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"strings"
)

type Capacity int

const (
	EMPTY Capacity = iota
	LOW
	MEDIUM
	HIGH
	FULL
)

type Event struct {
	Type string          `json:"type"`
	Data json.RawMessage `json:"data"`
}

var capacityToString = [...]string{"EMPTY", "LOW", "MEDIUM", "HIGH", "FULL"}

func (c *Capacity) String() string {
	if int(*c) < 0 || int(*c) >= len(capacityToString) {
		return "UNKNOWN"
	}
	return capacityToString[*c]
}

func (c Capacity) MarshalJSON() ([]byte, error) {
	return json.Marshal(c.String())
}

func (c *Capacity) UnmarshalJSON(data []byte) error {
	var str string
	if err := json.Unmarshal(data, &str); err != nil {
		return err
	}

	str = strings.ToUpper(str)
	for i, v := range capacityToString {
		if v == str {
			*c = Capacity(i)
			return nil
		}
	}

	return errors.New("invalid capacity value: " + str)
}

func generateRandomID(length int) (string, error) {
	bytes := make([]byte, length/2)
	if _, err := rand.Read(bytes); err != nil {
		return "", err
	}
	return hex.EncodeToString(bytes), nil
}

type Hello struct {
	Id     string `json:"id"`
	RoomId string `json:"roomId,omitempty"`
}

type SendCapacity struct {
	Id       string   `json:"id"`
	Capacity Capacity `json:"capacity"`
}

type Room struct {
	RoomId       int      `json:"roomId"`
	RoomCapacity Capacity `json:"roomCapacity"`
}
