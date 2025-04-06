package isel.leic.group25.utils

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

    for (row in sheet) {
        for (cell in row) {

            if (cell.toString() == ""){
                println("Empty cell")
            } else {
                println(cell.toString() + "\t")
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