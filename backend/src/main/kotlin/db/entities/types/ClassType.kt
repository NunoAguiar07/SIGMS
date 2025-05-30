package isel.leic.group25.db.entities.types

import kotlinx.serialization.Serializable

@Serializable
enum class ClassType {
    THEORETICAL,
    PRACTICAL,
    THEORETICAL_PRACTICAL;

    companion object {
        fun fromValue(value: String): ClassType? = entries.find { it.name == value }
    }
}