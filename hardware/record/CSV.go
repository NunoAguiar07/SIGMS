package record

import (
	"encoding/csv"
	"fmt"
	"os"
	"strconv"
	"time"
)

type CSVManager struct {
	filename string
}

func NewCSVManager(filename string) *CSVManager {
	csvManager := CSVManager{filename: filename}
	err := csvManager.CreateCSVFile()
	if err != nil {
		return nil
	}
	return &csvManager
}

func (cm *CSVManager) CreateCSVFile() error {
	if _, err := os.Stat(cm.filename); err == nil {
		return nil
	}

	file, err := os.Create(cm.filename + ".csv")
	if err != nil {
		return fmt.Errorf("error creating file: %w", err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	headers := []string{"time", "count"}
	if err := writer.Write(headers); err != nil {
		return fmt.Errorf("error writing headers: %w", err)
	}

	return nil
}

func (cm *CSVManager) WriteEntry(count int) error {
	if err := cm.CreateCSVFile(); err != nil {
		return err
	}

	file, err := os.OpenFile(cm.filename, os.O_WRONLY|os.O_APPEND, 0644)
	if err != nil {
		return fmt.Errorf("error opening file: %w", err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	currentTime := time.Now().Format("02/01/2006 15:04")

	record := []string{currentTime, strconv.Itoa(count)}

	if err := writer.Write(record); err != nil {
		return fmt.Errorf("error writing record: %w", err)
	}

	return nil
}
