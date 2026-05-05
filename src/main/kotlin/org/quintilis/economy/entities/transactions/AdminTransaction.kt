package org.quintilis.economy.entities.transactions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.quintilis.factions.annotations.Column
import org.quintilis.factions.annotations.PrimaryKey
import org.quintilis.factions.annotations.TableName
import org.quintilis.factions.cache.PlayerCache
import org.quintilis.factions.entities.BaseEntity
import org.quintilis.factions.entities.player.PlayerEntity
import java.util.UUID

@TableName("admin_transaction_details")
class AdminTransaction(
    @PrimaryKey
    @Column("transaction_id")
    val transactionId: Int,
    @Column("admin_id")
    val adminId: UUID
): BaseEntity() {
    fun getAdminPlayer(): Player?{
        return Bukkit.getPlayer(this.adminId)
    }

    fun getAdminPlayerEntity(playerDao: PlayerCache): PlayerEntity?{
        return playerDao.findById(this.adminId)
    }
}