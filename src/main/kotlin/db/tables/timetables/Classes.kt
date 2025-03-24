package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.types.ClassType
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Classes: Table<Class>("CLASS") {
    val id = int("id").primaryKey().bindTo { it.id }
    val subject = int("subject_id").references(Subjects){ it.subject }
    val type = varchar("type").transform({ ClassType.valueOf(it) }, {it.name}).bindTo { it.type }
    val startTime = long("start_time").bindTo { it.startTime }
    val endTime = long("end_time").bindTo { it.endTime }
}