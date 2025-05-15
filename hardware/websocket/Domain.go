package websocket

import (
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

func (c *Capacity) MarshalJSON() ([]byte, error) {
	if c == nil {
		return nil, errors.New("cannot marshal nil Capacity")
	}
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

type Hello struct {
	Id     string `json:"id"`
	RoomId string `json:"roomId"`
}

type SendCapacity struct {
	Id       string   `json:"id"`
	Capacity Capacity `json:"capacity"`
}

type Room struct {
	Capacity Capacity `json:"capacity"`
}
