package isel.leic.group25.db.repositories.ktorm

import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import org.ktorm.database.Database

class KTransaction(private val database: Database): TransactionInterface {
    override fun <T> useTransaction(transaction: () -> T): T {
        database.useTransaction{
            return transaction()
        }
    }
}