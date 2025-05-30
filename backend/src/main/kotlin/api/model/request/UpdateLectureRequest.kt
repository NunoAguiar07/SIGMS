package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.types.ClassType
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class UpdateLectureRequest(
    val newRoomId: Int,
    val newType: String,
    val newWeekDay: Int,
    val newStartTime: String,
    val newEndTime: String,
    val changeStartDate: String? = null,
    val changeEndDate: String? = null
) {
    fun validate(): RequestError? {
        return validateBasicFields() ?: validateTemporalFields()
    }

    private fun validateBasicFields(): RequestError? {
        return when {
            newRoomId <= 0 -> RequestError.Invalid("newRoomId")
            newStartTime.isBlank() -> RequestError.Missing("newStartTime")
            newEndTime.isBlank() -> RequestError.Missing("newEndTime")
            newType.isBlank() -> RequestError.Missing("newType")
            newType.uppercase() !in ClassType.entries.map { it.name } -> RequestError.Invalid("newType")
            newWeekDay !in 1..7 -> RequestError.Invalid("newWeekDay")
            else -> null
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun validateTemporalFields(): RequestError? {
        if (changeStartDate != null || changeEndDate != null) {
            val startDate = changeStartDate?.toInstantOrNull()
            val endDate = changeEndDate?.toInstantOrNull()

            return when {
                changeStartDate != null && startDate == null -> RequestError.Invalid("changeStartDate")
                changeEndDate != null && endDate == null -> RequestError.Invalid("changeEndDate")
                startDate != null && endDate != null && startDate >= endDate ->
                    RequestError.Invalid("changeStartDate must be before changeEndDate")
                else -> null
            }
        }
        return null
    }

    @OptIn(ExperimentalTime::class)
    fun parseChangeDates(): Pair<Instant?, Instant?> {
        return Pair(
            changeStartDate?.toInstantOrNull(),
            changeEndDate?.toInstantOrNull()
        )
    }

    // Extension function to parse String to Instant
    @OptIn(ExperimentalTime::class)
    private fun String.toInstantOrNull(): Instant? {
        return try {
            Instant.parse(this)
        } catch (e: Exception) {
            null
        }
    }
}
