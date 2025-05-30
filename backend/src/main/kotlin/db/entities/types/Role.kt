package isel.leic.group25.db.entities.types

enum class Role {
    STUDENT,
    TEACHER,
    ADMIN,
    TECHNICAL_SERVICE;

    companion object {
        fun fromValue(value: String): Role? = Role.entries.find { it.name == value }
    }
}