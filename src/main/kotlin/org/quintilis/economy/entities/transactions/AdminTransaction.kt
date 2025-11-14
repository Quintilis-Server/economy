package org.quintilis.economy.entities.transactions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName
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

    fun getAdminPlayerEntity(playerDao: PlayerDao): PlayerEntity?{
        return playerDao.findById(this.adminId)
    }
}