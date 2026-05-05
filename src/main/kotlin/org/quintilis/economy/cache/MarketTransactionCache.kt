package org.quintilis.economy.cache

import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.dao.MarketTransactionDao
import org.quintilis.economy.entities.transactions.MarketTransaction
import org.quintilis.factions.cache.AbstractDaoCache
import org.quintilis.factions.entities.player.PlayerEntity
import java.time.Duration

class MarketTransactionCache(
    private val marketTransactionDao: MarketTransactionDao, plugin: JavaPlugin,
): AbstractDaoCache<MarketTransactionDao, MarketTransaction, Int>(
    dao = marketTransactionDao,
    prefix = "market-transaction:",
    ttl = Duration.ofHours(2).seconds,
    classType = MarketTransaction::class.java, plugin,
), MarketTransactionDao by marketTransactionDao{
}