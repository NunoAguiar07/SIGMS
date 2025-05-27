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
func NewEWMA(weight float64) *EWMA {
	if weight < 0 {
		weight = 0
	} else if weight > 1 {
		weight = 1
	}

	return &EWMA{
		weight: weight,
		value:  0,
		init:   false,
	}
}

func (e *EWMA) ToCapacityLinear(max int, capacityRange int) int {
	if e.value <= 0 {
		return 0
	}
	if e.value >= float64(max) {
		return capacityRange - 1
	}
	scaled := e.value * float64(capacityRange-1) / float64(max)
	return int(math.Round(scaled))
}
