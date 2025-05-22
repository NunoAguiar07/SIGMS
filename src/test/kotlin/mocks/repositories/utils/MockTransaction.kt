package mocks.repositories.utils

import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable

class MockTransaction : Transactionable {
    override fun <T> useTransaction(transaction: () -> T): T = transaction()

    override fun <T> useTransaction(isolationLevel: IsolationLevel, transaction: () -> T): T =  transaction()
}