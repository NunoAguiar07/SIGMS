package isel.leic.group25.db.entities.types

enum class Status {
    PENDING,
    APPROVED,
    REJECTED;

    companion object {
        fun fromValue(value: String) : Status? = entries.find { it.name == value }
    }
}