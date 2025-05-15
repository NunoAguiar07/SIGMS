package main

type EWMA struct {
	weight float64
	value  float64
	init   bool
}

func (e *EWMA) Update(x float64) {
	if !e.init {
		e.value = x
		e.init = true
	} else {
		e.value = e.weight*x + (1-e.weight)*e.value
	}
}
