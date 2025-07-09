package packetsniffer

import "testing"

func TestCountMacs(t *testing.T) {
	count, err := countMACs()
	if err != nil {
		t.Error(err)
	}
	if count <= 0 {
		t.Error("Impossible to find negative or no number of MACs")
	}
}
