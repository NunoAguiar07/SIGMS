package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Subject
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Subjects: Table<Subject>("SUBJECT") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}