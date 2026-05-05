package org.quintilis.economy.entities.transactions

import org.quintilis.economy.cache.TransactionCache
import org.quintilis.factions.annotations.Column
import org.quintilis.factions.annotations.PrimaryKey
import org.quintilis.factions.annotations.TableName
import org.quintilis.factions.entities.BaseEntity
import org.quintilis.factions.cache.ChunkCache
import org.quintilis.factions.cache.CoreCache
import org.quintilis.factions.entities.chunk.ChunkEntity
import org.quintilis.factions.entities.clan.ClanCoreEntity

@TableName("chunk_transaction_details")
class ChunkTransaction(
    @PrimaryKey
    @Column("transaction_id")
    val transactionId: Int,

    @Column("chunk_id")
    val chunkId: Int,

    @Column("core_id")
    val coreId: Int,
): BaseEntity() {
    fun getChunkEntity(chunkCache: ChunkCache): ChunkEntity? {
        return chunkCache.findById(chunkId)
    }

    fun getCoreEntity(coreCache: CoreCache): ClanCoreEntity? {
        return coreCache.findById(coreId)
    }

    fun getTransaction(transactionCache: TransactionCache): Transaction? {
        return transactionCache.findById(this.transactionId)
    }
}