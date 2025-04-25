package mocks.repositories.utils

import isel.leic.group25.db.repositories.interfaces.TransactionInterface

class MockTransaction : TransactionInterface {
    override fun <T> useTransaction(transaction: () -> T): T = transaction()
}