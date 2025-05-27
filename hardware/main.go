package main

import (
	"hardware/packetsniffer"
	"hardware/persistance"
	"hardware/websocket"
	"log"
	"time"
)

const interval = 5 * 60
const url = "ws://localhost:8080/ws"

func main() {
	conn, err := websocket.EstablishConnection(url)
	for {
		if err != nil {
			log.Println("Error establishing websocket connection:", err)
			log.Println("Retrying in 10 seconds...")
			time.Sleep(10 * time.Second)
			conn, err = websocket.EstablishConnection("ws://localhost:8080/ws")
		} else {
			break
		}
	}
	ewmaChannel := make(chan packetsniffer.EWMA)
	signalChannel := make(chan struct{})
	device, err := persistance.Load()
	if err != nil {
		log.Fatal(err)
	}
	go packetsniffer.ReadAtIntervalOrByUpdate(interval, ewmaChannel, signalChannel)
	for {
		err := websocket.EventLoop(conn, device, ewmaChannel, signalChannel)
		if err != nil {
			log.Println(err)
			log.Println("Reestablishing connection in 10 seconds...")
			time.Sleep(10 * time.Second)
			conn, err = websocket.EstablishConnection(url)
		}
	}
}
