package isel.leic.group25.api.model.request

import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLectureRequest (
    val schoolClassId: Int,
    val roomId: Int,
    val type: ClassType,
    val weekDay: WeekDay,
    val startTime: String,
    val endTime: String,
    val newSchoolClassId: Int,
    val newRoomId: Int,
    val newType: ClassType,
    val newWeekDay: WeekDay,
    val newStartTime: String,
    val newEndTime: String
    )
