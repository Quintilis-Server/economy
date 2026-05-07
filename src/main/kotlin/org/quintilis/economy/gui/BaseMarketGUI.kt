package org.quintilis.economy.gui

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.GuiItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.market.MarketCategory
import org.quintilis.economy.services.EconomyServices.listingCache
import org.quintilis.economy.services.TransactionService
import org.quintilis.factions.entities.player.PlayerEntity
import org.quintilis.factions.extensions.asKyori
import org.quintilis.factions.extensions.sendTranslatable
import org.quintilis.factions.gui.BaseGUI
import org.quintilis.factions.services.FactionsServices
import org.quintilis.factions.services.FactionsServices.playerCache

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
            .asGuiItem { event ->
                val buyer = event.whoClicked as Player
                val buyerEntity = playerCache.getPlayer(buyer.uniqueId) ?: return@asGuiItem


                if (buyer.inventory.firstEmpty() == -1) {
                    handleTransactionResult(buyer, buyerEntity, TransactionService.TransactionResult.INVENTORY_FULL, listing)
                    return@asGuiItem
                }

                val amountToBuy = when {
                    event.isShiftClick -> listing.quantity // Compra tudo
                    event.isRightClick -> 1                // Compra 1 un
                    else -> 1                              // Padrão 1 un
                }

                Bukkit.getServer().globalRegionScheduler.execute(plugin) {

                    val result = TransactionService.buyPartialListing(buyerEntity, listing.id!!, amountToBuy)

                    buyer.scheduler.execute(plugin, {
                        handleTransactionResult(buyer, buyerEntity, result, listing, amountToBuy)
                    }, null, 0L)
                }
            }.apply {
                // Atualiza o Lore para explicar os controles
                val meta = this.itemStack.itemMeta
                val lore = meta.lore() ?: mutableListOf()

                lore.add(Component.empty())
                lore.addAll(
                    transLore("market.item.details",
                        Placeholder.unparsed("price", listing.askingPricePerItem.toString()),
                        Placeholder.unparsed("seller", listing.getSellerPlayer()?.name ?: "Offline")
                    )
                )
                lore.add(Component.empty())
                lore.add(mm.deserialize("<yellow> Botão Esquerdo: <white>Comprar 1un"))
                lore.add(mm.deserialize("<yellow> Shift + Clique: <white>Comprar tudo"))

                meta.lore(lore)
                this.itemStack.itemMeta = meta
            }
        }

    private fun handleTransactionResult(
        player: Player,
        playerEntity: PlayerEntity,
        result: TransactionService.TransactionResult,
        listing: Listing,
        quantity: Int? = null
    ) {
        when (result) {
            is TransactionService.TransactionResult.SUCCESS_PARTIAL -> {
                val boughtItem = listing.getItem()
                boughtItem.amount = result.finalQuantity

                // Entrega o item (se sobrou algo que não coube, dropa no chão por segurança)
                val leftover = player.inventory.addItem(boughtItem)
                leftover.values.forEach { player.world.dropItem(player.location, it) }

                player.sendTranslatable(
                    "market.purchase.success",
                    Argument.numeric("amount", result.finalQuantity),
                    Argument.component("item", boughtItem.displayName()),
                    Argument.numeric("price", listing.askingPricePerItem * result.finalQuantity)
                )
                player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP.asKyori())

                // Atualiza a página para refletir o novo estoque do mercado
                loadItems()
            }

            TransactionService.TransactionResult.INSUFFICIENT_FUNDS -> {
                val nedded =  listing.askingPricePerItem * (quantity ?: 1)
                        player.sendTranslatable(
                    "market.purchase.error.insufficient_funds",
                    Argument.numeric("needed", nedded),
                    Argument.numeric("have", playerEntity.points)
                )
                player.playSound(Sound.ENTITY_VILLAGER_NO.asKyori())
            }

            TransactionService.TransactionResult.INVENTORY_FULL -> {
                player.sendTranslatable("market.purchase.error.inventory_full")
                player.playSound(Sound.ENTITY_VILLAGER_NO.asKyori())
            }

            TransactionService.TransactionResult.NOT_FOUND -> {
                player.sendTranslatable("market.purchase.error.not_found")
                player.playSound(Sound.ENTITY_ITEM_BREAK.asKyori())
                loadItems() // Alguém comprou antes, atualiza a tela!
            }

            TransactionService.TransactionResult.SELLER_IS_BUYER -> {
                player.sendTranslatable("market.purchase.error.seller_is_buyer")
                player.playSound(Sound.ENTITY_VILLAGER_NO.asKyori())
            }

            else -> {
                player.sendTranslatable("market.purchase.error.generic")
            }


        }
    }
}