package isel.leic.group25.db.entities.types

import kotlinx.serialization.Serializable


@Serializable
enum class WeekDay(val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    companion object {
        fun fromValue(value: Int): WeekDay? = entries.find { it.value == value }
        fun fromValueDB(value: Int): WeekDay = entries.first { it.value == value }
    }
}