package isel.leic.group25.db.repositories.ktorm

import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import org.ktorm.database.Database
import org.ktorm.database.TransactionIsolation

class KTransaction(private val database: Database): TransactionInterface {
    override fun <T> useTransaction(transaction: () -> T): T {
        database.useTransaction{
            return transaction()
        }
    }

    override fun <T> useTransaction(isolationLevel: IsolationLevel, transaction: () -> T): T {
        database.useTransaction(TransactionIsolation.valueOf(isolationLevel.name.uppercase())){
            return transaction()
        }
    }
}