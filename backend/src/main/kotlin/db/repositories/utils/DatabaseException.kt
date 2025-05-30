package isel.leic.group25.db.repositories.utils

import java.sql.SQLException

inline fun <T> withDatabase(block: () -> T): T {
    return try {
        block()
    } catch (e: SQLException) {
        throw e
    }
}