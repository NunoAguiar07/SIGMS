package isel.leic.group25.db.repositories.users.errors

sealed class DbError {
    data class ConnectionError(val message: String = "Failed to connect to database") : DbError()
    data class QueryError(val message: String) : DbError()
}