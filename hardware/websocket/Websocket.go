package websocket

import (
	"encoding/json"
	"errors"
	"github.com/gorilla/websocket"
	"hardware/packetsniffer"
	"hardware/persistance"
	"log"
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
	if device.RoomId != 0 {
		hello.RoomId = device.RoomId
	}
	rawHello, err := json.Marshal(hello)
	event := Event{"hello", rawHello}
	if conn != nil {
		log.Println("Greeting Server")
		return conn.WriteJSON(event)
	}
	return errors.New("failed to establish connection")
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
			log.Println("Sending EWMA")
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
				log.Println("Received Event")
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
	for {
		_, rawMessage, err := conn.ReadMessage()
		if err != nil {
			errorChannel <- err
		}
		var event Event
		err = json.Unmarshal(rawMessage, &event)
		if err != nil {
			errorChannel <- err
		} else {
			eventChannel <- &event
		}
	}
}

func parseEvent(event *Event, device *persistance.Device, signalRead chan struct{}) error {
	switch event.Type {
	case "room":
		{
			log.Println("Updating Room")
			var room Room
			err := json.Unmarshal(event.Data, &room)
			if err != nil {
				return err
			}
			device.RoomCapacity = room.RoomCapacity
			device.RoomId = room.RoomId
			err = device.Save()
			if err != nil {
				return err
			}
		}
	case "updateCapacity":
		log.Println("Updating Capacity")
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
