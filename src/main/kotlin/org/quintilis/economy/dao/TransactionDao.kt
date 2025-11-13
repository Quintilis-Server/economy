package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.statement.SqlQuery

interface TransactionDao: BaseDao {
    @SqlQuery("")
    fun getTransactionsByPlayer()
}