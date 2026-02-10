package org.quintilis.economy.cache

import org.quintilis.economy.dao.TransactionDao
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.factions.cache.AbstractDaoCache

class TransactionCache(
    private val transactionDao: TransactionDao,
): AbstractDaoCache<TransactionDao, Transaction, Int>(
    dao = transactionDao,
    prefix = "transactions:",
    ttl = 1200,
    classType = Transaction::class.java,
) {
}