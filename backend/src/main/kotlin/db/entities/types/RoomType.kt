package isel.leic.group25.db.entities.types

import kotlinx.serialization.Serializable

@Serializable
enum class RoomType {
    OFFICE,
    CLASS,
    STUDY;

    companion object {
        fun fromValue(value: String): RoomType? = entries.find { it.name == value }
    }
}