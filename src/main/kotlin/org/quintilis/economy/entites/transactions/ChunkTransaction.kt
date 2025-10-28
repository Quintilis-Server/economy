package org.quintilis.economy.entites.transactions

import lombok.NoArgsConstructor
import org.quintilis.economy.entites.BaseEntity
import org.quintilis.economy.entites.annotations.*
import java.util.UUID

@TableName("chunk_transaction_details")
data class ChunkTransaction(
    @Column("transaction_id")
    @PrimaryKey
    val transactionId: Int,
    @Column("world_uuid")
    val worldUUID: UUID,
    @Column("chunk_x")
    val chunkX: Int,
    @Column("chunk_y")
    val chunkY: Int,
    @Column("clan_id")
    val clanId: Int,
): BaseEntity() {
}