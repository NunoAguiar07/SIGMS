package packetsniffer

import "math"

type EWMA struct {
	weight float64
	value  float64
	init   bool
}

func (e *EWMA) Update(newValue int) {
	x := float64(newValue)
	if !e.init {
		e.value = x
		e.init = true
	} else {
		e.value = e.weight*x + (1-e.weight)*e.value
	}
}

func (e *EWMA) ToCapacityLinear(max float64, capacityRange int) int {
	if e.value <= 0 {
		return 0
	}
	if e.value >= max {
		return capacityRange - 1
	}
	scaled := e.value * float64(capacityRange-1) / max
	return int(math.Round(scaled))
}
