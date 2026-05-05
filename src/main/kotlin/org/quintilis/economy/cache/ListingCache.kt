package org.quintilis.economy.cache

import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.factions.cache.AbstractDaoCache

class ListingCache(
    private val listingDao: ListingDao, plugin: JavaPlugin,
): AbstractDaoCache<ListingDao, Listing, Int>(
    dao = listingDao,
    prefix = "listing:",
    ttl = 1200,
    classType = Listing::class.java, plugin,
), ListingDao by listingDao {
}