package isel.leic.group25.db.repositories.interfaces

interface TransactionInterface {
    fun <T> useTransaction(transaction: () -> T): T
}