package isel.leic.group25.db.repositories.interfaces

interface TransactionInterface {
    fun <T> useTransaction(transaction: () -> T): T
    fun <T> useTransaction(isolationLevel: IsolationLevel, transaction: () -> T): T
}

enum class IsolationLevel {
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}