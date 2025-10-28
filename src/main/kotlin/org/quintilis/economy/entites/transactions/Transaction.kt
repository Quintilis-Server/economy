package org.quintilis.economy.entites.transactions

import lombok.NoArgsConstructor
import org.quintilis.economy.entites.BaseEntity
import org.quintilis.economy.entites.annotations.Column
import org.quintilis.economy.entites.annotations.PrimaryKey
import org.quintilis.economy.entites.annotations.TableName
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