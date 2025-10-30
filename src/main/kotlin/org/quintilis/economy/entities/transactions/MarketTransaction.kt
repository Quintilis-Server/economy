package org.quintilis.economy.entities.transactions

import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName

@TableName("market_transactions_details")
data class MarketTransaction(
    @PrimaryKey
    @Column("transaction_id")
    val transactionId: Int,

    @Column("listing_id")
    val listingId: Int,

    @Column("item_id")
    val itemId: Int,

    @Column("quantity")
    val quantity: Int,

    @Column("price_per_item")
    val pricePerItem: Int
): BaseEntity()
