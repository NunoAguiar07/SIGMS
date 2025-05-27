package packetsniffer

import (
	"bufio"
	"log"
	"os"
	"os/exec"
	"time"
)

const sniffPacketsCommand = "tshark -I -i wlan1 -a duration:180 -w capture.pcap"
const createFilteredTXTCommand = "tshark -r capture.pcap -Y \"wlan.fc.type_subtype == 4\" -T fields -E separator=, -e wlan.sa > macs.txt"

func countMACs() int {
	err := exec.Command("sh", "-c", sniffPacketsCommand).Run()
	if err != nil {
		log.Fatalf("Error: %v\n", err)
	} else {
		log.Printf("Successfully read MACs")
	}

	err = exec.Command("sh", "-c", createFilteredTXTCommand).Run()
	if err != nil {
		log.Fatalf("Error: %v\n", err)
	} else {
		log.Printf("Successfully read MACs")
	}
	return readUniqueMACs()
}

func addIfNotExists(list *[]string, item string) {
	for _, v := range *list {
		if v == item {
			return
		}
	}
	*list = append(*list, item)
}

func readUniqueMACs() int {
	var macs []string
	file, err := os.Open("macs.txt")
	if err != nil {
		log.Fatalf("Error opening file: %v\n", err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatalf("Error closing file: %v\n", err)
		}
	}(file)
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		mac := scanner.Text()
		addIfNotExists(&macs, mac)
	}

	if err := scanner.Err(); err != nil {
		log.Fatalf("Error reading file: %v\n", err)
	}

	return len(macs)
}

func ReadAtIntervalOrByUpdate(interval int, ewmaChannel chan EWMA, signalRead chan struct{}) {
	ticker := time.NewTicker(time.Duration(interval) * time.Second)
	ewma := NewEWMA(0.25)
	readUpdateAndSave(ewma, ewmaChannel)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			readUpdateAndSave(ewma, ewmaChannel)
		case <-signalRead:
			readUpdateAndSave(ewma, ewmaChannel)
		}
	}
}

func readUpdateAndSave(ewma *EWMA, ewmaChannel chan EWMA) {
	count := countMACs()
	ewma.Update(count)
	ewmaChannel <- *ewma
}
