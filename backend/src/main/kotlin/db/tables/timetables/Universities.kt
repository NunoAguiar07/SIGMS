package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.University
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Universities: Table<University>("university") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("university_name").bindTo { it.name }
}