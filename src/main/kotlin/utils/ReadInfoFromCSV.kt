package isel.leic.group25.utils

import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream


data class ClassInfo(
    val name: String,
    val groups: List<String>,
    val teachers: List<String>,
    val rooms: List<String>,
    val types: List<String>,
    val day: String,
    val startTime: String,
    val endTime: String
)

fun parseScheduleFromExcel(filePath: String): List<ClassInfo> {
    val file = FileInputStream(filePath)
    val workbook = XSSFWorkbook(file)
    val sheet = workbook.getSheetAt(0)

    val dayMap = mapOf(
        1 to "Segunda-feira",
        2 to "Terça-feira",
        3 to "Quarta-feira",
        4 to "Quinta-feira",
        5 to "Sexta-feira",
        6 to "Sábado",
        7 to "Domingo"
    )
    val dateFormat = java.text.SimpleDateFormat("HH:mm")

    val timeSlots = mutableMapOf<Int, String>()
    for (row in 1..sheet.lastRowNum) {
        val timeCell = sheet.getRow(row)?.getCell(0)
        if (timeCell != null) {
            if (DateUtil.isCellDateFormatted(timeCell)) {
                timeSlots[row] = dateFormat.format(timeCell.dateCellValue)
            } else {
                timeSlots[row] = timeCell.toString().trim()
            }
        }
    }

    val classes = mutableListOf<ClassInfo>()
    val mergedRegions = sheet.mergedRegions

    for (mergedRegion in mergedRegions) {
        val startRow = mergedRegion.firstRow
        val endRow = mergedRegion.lastRow
        val col = mergedRegion.firstColumn

        val cell = sheet.getRow(startRow).getCell(col)
        val rawText = cell?.toString()?.trim() ?: continue

        val regex = Regex("""([^\s\[\(\]]+)|(\[[^\]]+\])|(\([^\)]+\))|(\(\[[^\]]+\]\))""")
        val tokens = regex.findAll(rawText).map { it.value.trim() }.toList()

        if (tokens.isEmpty()) continue

        val name = tokens[0].trim()

        var groups = emptyList<String>()
        var teachers = emptyList<String>()
        var rooms = emptyList<String>()
        var types = emptyList<String>()

        tokens.drop(1).forEach { token ->
            when {
                token.startsWith("[") && token.endsWith("]") -> {
                    val content = token.removeSurrounding("[", "]")
                    when {
                         content.contains(".")  -> {
                            rooms = content.split(";").map { it.trim() }
                        }
                        content.matches(Regex(".*\\d.*")) -> {
                            groups = content.split(";").map { it.trim() }
                        }
                        content.length <= 3 && content.uppercase() == content -> {
                            types = content.split(";").map { it.trim() }
                        }
                    }
                }
                token.startsWith("([") || token.startsWith("[(") || token.startsWith("(") -> {
                    val content = token.removePrefix("([")
                        .removePrefix("[(")
                        .removePrefix("(")
                        .removeSuffix("])")
                        .removeSuffix(")]")
                        .removeSuffix(")")
                    teachers = content.split(";").map { it.trim() }
                }
            }
        }

        val day = dayMap[col] ?: continue
        val startTime = timeSlots[startRow] ?: continue
        val endTime = timeSlots[endRow + 1] ?: continue

        classes.add(
            ClassInfo(
                name = name,
                groups = groups,
                teachers = teachers,
                rooms = rooms,
                types = types,
                day = day,
                startTime = startTime,
                endTime = endTime
            )
        )
    }

    file.close()
    workbook.close()

    return classes
}



fun main() {
        val classes = parseScheduleFromExcel("C:\\Users\\User\\IdeaProjects\\FinalProject\\src\\main\\resources\\HorarioTeste.xlsx")
    classes.forEach {
        println(" ${it.day}: ${it.name}")
        println("  From ${it.startTime} to ${it.endTime}")
        println("  Groups: ${it.groups.joinToString(", ")}")
        println("  Teachers: ${it.teachers.joinToString(", ")}")
        println("  Rooms: ${it.rooms.joinToString(", ")}")
        println("  Types: ${it.types.joinToString(", ")}")
        println()
    }

}
