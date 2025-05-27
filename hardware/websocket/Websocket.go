package websocket

import (
	"encoding/json"
	"errors"
	"github.com/gorilla/websocket"
	"hardware/packetsniffer"
	"hardware/persistance"
	"net"
	"strconv"
)

func EstablishConnection(url string) (conn *websocket.Conn, err error) {
	conn, _, err = websocket.DefaultDialer.Dial(url, nil)
	if err != nil {
		return nil, err
	}
	return conn, nil
}

func greetBackend(conn *websocket.Conn, device *persistance.Device) error {
	var err error
	if device.Id == "" {
		device.Id, err = generateRandomID(16)
	}
	if err != nil {
		return err
	}
	hello := Hello{Id: device.Id}
	if device.RoomId != "" {
		hello.RoomId = device.RoomId
	}
	rawHello, err := json.Marshal(hello)
	event := Event{"hello", rawHello}
	return conn.WriteJSON(event)
}

func EventLoop(conn *websocket.Conn, device *persistance.Device, ewmaChannel chan packetsniffer.EWMA, signalRead chan struct{}) error {
	eventChannel := make(chan *Event)
	errorChannel := make(chan error)
	err := greetBackend(conn, device)
	if err != nil {
		return err
	}
	go awaitMessage(conn, eventChannel, errorChannel)
	for {
		select {
		case device.Ewma = <-ewmaChannel:
			err := sendCapacity(device, conn)
			if err != nil {
				return err
			}
			err = device.Save()
			if err != nil {
				return err
			}
		case event := <-eventChannel:
			{
				err := parseEvent(event, device, signalRead)
				if err != nil {
					return err
				}
			}
		case err := <-errorChannel:
			{
				return err
			}
		}

	}
}
func awaitMessage(conn *websocket.Conn, eventChannel chan *Event, errorChannel chan error) {
	_, rawMessage, err := conn.ReadMessage()
	if err != nil {
		var netErr net.Error
		if !errors.As(err, &netErr) || !netErr.Timeout() {
			errorChannel <- err
		}
		if errors.As(err, &netErr) && netErr.Timeout() {
			errorChannel <- err
		}
	}
	var event Event
	err = json.Unmarshal(rawMessage, &event)
	if err != nil {
		errorChannel <- err
	}
	eventChannel <- &event
}

func parseEvent(event *Event, device *persistance.Device, signalRead chan struct{}) error {
	switch event.Type {
	case "room":
		{
			var room Room
			err := json.Unmarshal(event.Data, &room)
			if err != nil {
				return err
			}
			device.RoomCapacity = int(room.RoomCapacity)
			device.RoomId = strconv.Itoa(room.RoomId)
			err = device.Save()
			if err != nil {
				return err
			}
		}
	case "updateCapacity":
		signalRead <- struct{}{}
	}
	return nil
}

func sendCapacity(device *persistance.Device, conn *websocket.Conn) error {
	id := device.Id
	capacity := device.Ewma.ToCapacityLinear(device.RoomCapacity, 5)
	sendCapacity := SendCapacity{
		Id:       id,
		Capacity: Capacity(capacity),
	}
	rawSendCapacity, err := json.Marshal(sendCapacity)
	if err != nil {
		return err
	}
	event := Event{"receiveCapacity", rawSendCapacity}
	return conn.WriteJSON(event)
}
