package websocket

import (
	"encoding/json"
	"errors"
	"github.com/gorilla/websocket"
	"hardware/packetsniffer"
	"hardware/persistance"
	"log"
	"net"
	"strconv"
	"time"
)

func EstablishConnection(url string) (conn *websocket.Conn, err error) {
	conn, _, err = websocket.DefaultDialer.Dial(url, nil)
	if err != nil {
		return nil, err
	}
	return conn, nil
}

func greetBackend(conn *websocket.Conn, device *persistance.Device) error {
	var id string
	var err error
	if device.Id == ""{
		id, err = generateRandomID(16)
	} else {
		id = device.Id
	}
	if err != nil {
		return err
	}
	hello := Hello{Id: id}
	if device.RoomId != "" {
		hello.RoomId = device.RoomId
	}
	rawHello, err := json.Marshal(hello)
	event := Event{"receiveCapacity", rawHello}
	return conn.WriteJSON(event)
}

func EventLoop(conn *websocket.Conn, device *persistance.Device, ewmaChannel chan packetsniffer.EWMA, signalRead chan struct{}) error {
	err := greetBackend(conn, device)
	if err != nil {
		return err
	}
	for {
		select {
		case ewma := <-ewmaChannel:
			err := sendCapacity(ewma, device, conn)
			if err != nil {
				return err
			}
		default:
			{
				err := awaitMessage(device, signalRead, conn)
				if err != nil {
					return err
				} else {
					continue
				}
			}
		}
	}
}
func awaitMessage(device *persistance.Device, signalRead chan struct{}, conn *websocket.Conn) error {
	err := conn.SetReadDeadline(time.Now().Add(100 * time.Millisecond))
	if err != nil {
		return err
	}
	_, rawMessage, err := conn.ReadMessage()
	if err != nil {
		var netErr net.Error
		if !errors.As(err, &netErr) || !netErr.Timeout() {
			log.Printf("WebSocket error: %v", err)
			return err
		}
	}
	var event Event
	err = json.Unmarshal(rawMessage, &event)
	if err != nil {
		return err
	}
	return parseEvent(event, device, signalRead)
}

func parseEvent(event Event, device *persistance.Device, signalRead chan struct{}) error {
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

func sendCapacity(ewma packetsniffer.EWMA, device *persistance.Device, conn *websocket.Conn) error {
	id := device.Id
	capacity := ewma.ToCapacityLinear(5, device.RoomCapacity)
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
