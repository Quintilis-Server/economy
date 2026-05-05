package org.quintilis.economy.gui

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.GuiItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.market.MarketCategory
import org.quintilis.economy.services.EconomyServices.listingCache
import org.quintilis.factions.extensions.sendTranslatable
import org.quintilis.factions.gui.BaseGUI

class BaseMarketGUI(
    player: Player,
    private val category: MarketCategory,
    plugin: JavaPlugin
): BaseGUI(player, category.displayTag, plugin = plugin) {
    override fun loadItems() {
        // Chamamos o motor de paginação da Base
        loadPageData(
            page = 1,
            fetcher = {
                listingCache.getListingByCategory(category, (currentPageIndex - 1) * pageSize, pageSize)
            },
            totalFetcher = {
                listingCache.getTotalByCategory(category)
            },
            itemMapper = { listing ->
                createMarketItem(listing)
            }
        )
    }

    private fun createMarketItem(listing: Listing): GuiItem {
        return ItemBuilder.from(listing.getItem())
            .asGuiItem { /* lógica de compra */ }
    }
}