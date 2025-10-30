package org.quintilis.economy.entities.transactions

import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName
import java.util.Date
import java.util.UUID

@TableName("transactions")
open class Transaction(
    @PrimaryKey
    val id: Int?,
    @Column("player_id")
    val playerId: UUID?,
    @Column("transaction_type")
    val transactionType: TransactionType?,
    @Column("change")
    val change: Int?,
    @Column("time")
    val timestamp: Date?,
): BaseEntity() {
}