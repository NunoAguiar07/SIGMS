package packetsniffer

import (
	"bufio"
	"log"
	"os"
	"os/exec"
)

const sniffPacketsCommand = "tshark -I -i wlan1 -Y \"wlan.fc.type_subtype == 4\" -a duration:30 -w capture.pcap"
const createFilteredTXTCommand = "tshark -r capture.pcap -Y \"wlan.fc.type_subtype == 4\" -T fields -E separator=, -e wlan.sa > macs.txt"

func CountMACs() int {
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

func countListItems(list []string) int {
	count := 0
	for _, _ = range list {
		count++
	}
	return count
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

	return countListItems(macs)
}
