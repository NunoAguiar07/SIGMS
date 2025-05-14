package main

type RoomInformation

func AverageProbeWithPacketEWMA(probeEWMA EWMA, packetEWMA EWMA) float64 {
	return probeEWMA.value*0.2 + packetEWMA.value*0.8
}
