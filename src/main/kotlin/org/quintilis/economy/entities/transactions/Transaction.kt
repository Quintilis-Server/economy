package org.quintilis.economy.entities.transactions

import org.quintilis.factions.annotations.Column
import org.quintilis.factions.annotations.PrimaryKey
import org.quintilis.factions.annotations.TableName
import java.util.Date
import java.util.UUID
import java.time.Instant
import org.quintilis.factions.entities.BaseEntity

@TableName("transactions")
open class Transaction(
    @PrimaryKey
    val id: Int? = null,
    @Column("player_id")
    val playerId: UUID,
    @Column("transaction_type")
    val transactionType: TransactionType,
    @Column("change")
    val change: Int,
    @Column("time")
    val timestamp: Instant = Instant.now(),
    @Column("parent_id")
    val parentId: Int? = null,
): BaseEntity() {
}