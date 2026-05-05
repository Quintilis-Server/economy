package org.quintilis.economy.entities.transactions

import org.quintilis.factions.annotations.Column
import org.quintilis.factions.annotations.PrimaryKey
import org.quintilis.factions.annotations.TableName
import org.quintilis.factions.entities.BaseEntity

@TableName("market_transactions_details")
data class MarketTransaction(
    @PrimaryKey
    @Column("transaction_id")
    val transactionId: Int,

    @Column("listing_id")
    val listingId: Int,

    @Column("quantity")
    val quantity: Int,

): BaseEntity()
