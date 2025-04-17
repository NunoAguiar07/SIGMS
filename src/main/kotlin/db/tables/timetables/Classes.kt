package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Class
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Classes: Table<Class>("class") {
    val id = int("id").primaryKey().bindTo { it.id }
    val subject = int("subject_id").references(Subjects){ it.subject }
    val name = varchar("class_name").bindTo { it.name }
}