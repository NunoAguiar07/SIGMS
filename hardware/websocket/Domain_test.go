package websocket

import (
	"encoding/json"
	"strconv"
	"testing"
)

func TestGenerateRandomID(t *testing.T) {
	randomID, err := generateRandomID(16)
	if err != nil {
		t.Error(err)
	}
	if len(randomID) != 16 {
		t.Error("Invalid room ID length: " + strconv.Itoa(len(randomID)))
	}
}

func TestCapacity_MarshalJSON(t *testing.T) {
	capacity := Capacity(1)
	result, err := json.Marshal(capacity)
	if err != nil {
		t.Error(err)
	}
	if string(result) != "\"LOW\"" {
		t.Error("Invalid capacity JSON")
	}
}

func TestCapacity_UnmarshalJSON(t *testing.T) {
	var capacity Capacity
	err := json.Unmarshal([]byte(`"LOW"`), &capacity)
	if err != nil {
		t.Error(err)
	}
	if capacity != 1 {
		t.Error("Invalid JSON capacity")
	}
}

func TestMarshalHello(t *testing.T) {
	hello := Hello{"23", 3}
	result, err := json.Marshal(hello)
	if err != nil {
		t.Error(err)
	}
	if string(result) != `{"id":"23","roomId":3}` {
		t.Error("Invalid Hello JSON")
	}
}

func TestMarshalSendCapacity(t *testing.T) {
	sendCapacity := SendCapacity{"23", 1}
	result, err := json.Marshal(sendCapacity)
	if err != nil {
		t.Error(err)
	}
	if string(result) != `{"id":"23","capacity":"LOW"}` {
		t.Error("Invalid SendCapacity JSON")
	}
}

func TestMarshalEvent(t *testing.T) {
	hello := Hello{"23", 3}
	helloJson, err := json.Marshal(hello)
	if err != nil {
		t.Error(err)
	}
	event := Event{"hello", helloJson}
	result, err := json.Marshal(event)
	if err != nil {
		t.Error(err)
	}
	if string(result) != `{"type":"hello","data":{"id":"23","roomId":3}}` {
		t.Error("Invalid Event JSON")
	}
}

func TestUnmarshalEvent(t *testing.T) {
	jsonString := `{"type":"hello","data":{"id":"23","roomId":3}}`
	var event Event
	err := json.Unmarshal([]byte(jsonString), &event)
	if err != nil {
		t.Error(err)
	}
	hello := Hello{"23", 3}
	helloJson, err := json.Marshal(hello)
	if err != nil {
		t.Error(err)
	}
	eventToBeCompared := Event{"hello", helloJson}
	if eventToBeCompared.Type != event.Type {
		t.Error("Invalid event type")
	}
	var dataHello Hello
	err = json.Unmarshal(event.Data, &dataHello)
	if err != nil {
		t.Error(err)
	}
	if hello.Id != dataHello.Id {
		t.Error("Invalid Event ID")
	}
	if hello.RoomId != dataHello.RoomId {
		t.Error("Invalid Event Room ID")
	}
}
