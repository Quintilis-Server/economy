package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.quintilis.economy.entities.transactions.MarketTransaction
import org.quintilis.factions.dao.BaseDao

interface MarketTransactionDao: BaseDao<MarketTransaction, Int> {
    @SqlQuery("SELECT * FROM market_transaction_details WHERE listing_id = :listingId")
    fun findByListingId(@Bind("listingId") listingId: Int): List<MarketTransaction>

    @SqlQuery("SELECT * FROM market_transaction_details WHERE transaction_id = :transactionId")
    fun findByTransactionId(@Bind("transactionId") transactionId: Int): MarketTransaction?
}