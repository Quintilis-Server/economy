package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.factions.dao.BaseDao
import java.util.UUID

interface TransactionDao: BaseDao<Transaction, Int> {
    @SqlQuery("SELECT * FROM transactions WHERE player_id = :player_id order by time desc limit :limit")
    fun getTransactionsByPlayer(@Bind("player_id") playerId: UUID, @Bind("limit") limit: Int = 10): List<Transaction>
}