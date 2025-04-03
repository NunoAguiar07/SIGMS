package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.types.ClassType
import kotlinx.datetime.Instant
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Classes: Table<Class>("CLASS") {
    val id = int("id").primaryKey().bindTo { it.id }
    val subject = int("subject_id").references(Subjects){ it.subject }
    val type = varchar("class_type").transform({ ClassType.valueOf(it.uppercase()) }, {it.name.lowercase()}).bindTo { it.type }
    val startTime = long("start_time").transform({ Instant.fromEpochMilliseconds(it)},{it.toEpochMilliseconds()}).bindTo { it.startTime }
    val endTime = long("end_time").transform({ Instant.fromEpochMilliseconds(it)},{it.toEpochMilliseconds()}).bindTo { it.endTime }
}