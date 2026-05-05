package org.quintilis.economy.cache

import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.dao.AdminTransactionDao
import org.quintilis.economy.entities.transactions.AdminTransaction
import org.quintilis.factions.cache.AbstractDaoCache
import java.time.Duration

class AdminTransactionCache(
    private val adminTransactionDao: AdminTransactionDao, plugin: JavaPlugin
): AbstractDaoCache<AdminTransactionDao, AdminTransaction, Int>(
    dao = adminTransactionDao,
    prefix = "admin-transaction:",
    ttl = Duration.ofHours(2).seconds,
    classType = AdminTransaction::class.java, plugin,
), AdminTransactionDao by adminTransactionDao {
}