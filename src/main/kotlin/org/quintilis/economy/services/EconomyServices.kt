package org.quintilis.economy.services

import org.quintilis.economy.Economy
import org.quintilis.economy.cache.AdminTransactionCache
import org.quintilis.economy.cache.ListingCache
import org.quintilis.economy.cache.MarketTransactionCache
import org.quintilis.economy.cache.TransactionCache
import org.quintilis.economy.dao.AdminTransactionDao
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.dao.MarketTransactionDao
import org.quintilis.economy.dao.TransactionDao
import org.quintilis.factions.managers.DatabaseManager

/**
 * Singleton de serviços - ponto central de acesso a DAOs e Caches.
 * Evita criar múltiplas instâncias e facilita injeção de dependência.
 */
object EconomyServices {

    private lateinit var plugin: Economy

    fun init(plugin: Economy) {
        this.plugin = plugin
    }

    val transactionDao: TransactionDao by lazy {
        DatabaseManager.getDAO(TransactionDao::class)
    }

    val listingDao: ListingDao by lazy {
        DatabaseManager.getDAO(ListingDao::class)
    }

    val marketTransactionDao: MarketTransactionDao by lazy {
        DatabaseManager.getDAO(MarketTransactionDao::class)
    }

    val adminTransactionDao: AdminTransactionDao by lazy {
        DatabaseManager.getDAO(AdminTransactionDao::class)
    }


    val transactionCache: TransactionCache by lazy {
        TransactionCache(transactionDao, plugin)
    }

    val listingCache: ListingCache by lazy {
        ListingCache(listingDao, plugin)
    }

    val marketTransactionCache: MarketTransactionCache by lazy {
        MarketTransactionCache(marketTransactionDao, plugin)
    }

    val adminTransactionCache: AdminTransactionCache by lazy {
        AdminTransactionCache(adminTransactionDao, plugin)
    }

}