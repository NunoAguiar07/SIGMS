package isel.leic.group25.db.entities.types

enum class Role {
    STUDENT,
    TEACHER,
    ADMIN,
    TECHNICAL_SERVICE
}

fun String.toRoleOrNull(): Role? {
    return Role.entries.firstOrNull {
        it.name.equals(this.trim(), ignoreCase = true)
    }
}