package isel.leic.group25.utils

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

data class TimetableEntry(
    val timeRange: String,
    val monday: String?,
    val tuesday: String?,
    val wednesday: String?,
    val thursday: String?,
    val friday: String?
)

data class Timetable(val entries: List<TimetableEntry>)

fun parseTimetable(text: String): Timetable {
    val lines = text.lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
    val headers = lines.first().split("\\s+".toRegex())
    val entries = lines.drop(1).map { line ->
        val columns = line.split("\\s+".toRegex())
        val timeRange = "${columns[0]} - ${columns[1]}"
        TimetableEntry(
            timeRange = timeRange,
            monday = columns.getOrNull(2)?.takeIf { it.isNotBlank() },
            tuesday = columns.getOrNull(3)?.takeIf { it.isNotBlank() },
            wednesday = columns.getOrNull(4)?.takeIf { it.isNotBlank() },
            thursday = columns.getOrNull(5)?.takeIf { it.isNotBlank() },
            friday = columns.getOrNull(6)?.takeIf { it.isNotBlank() }
        )
    }

    return Timetable(entries)
}

fun readExcelFile(filePath: String) {
    val fis = FileInputStream(filePath)
    val workbook = XSSFWorkbook(fis)
    val sheet = workbook.getSheetAt(0)
    val dateFormat = java.text.SimpleDateFormat("HH:mm")

    for (row in sheet) {
        for (cell in row) {
            val cellValue = when (cell.cellType) {
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Format time values properly
                        dateFormat.format(cell.dateCellValue)
                    } else {
                        cell.numericCellValue.toString()
                    }
                }
                CellType.STRING -> cell.stringCellValue
                CellType.BLANK -> ""
                else -> cell.toString()
            }

            if (cellValue.isEmpty()) {
                print("Empty cell\t")
            } else {
                print("$cellValue\t")
            }
        }
        println()
    }

    workbook.close()
    fis.close()
}

fun main() {
    val csvContent = readExcelFile("C:\\Users\\User\\IdeaProjects\\FinalProject\\src\\main\\resources\\horarioNovo.xlsx")
/*
    val timetable = parseTimetable(csvContent)
    println(timetable)*/
}