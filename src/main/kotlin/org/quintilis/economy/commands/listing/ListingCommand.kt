package org.quintilis.economy.commands.listing

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.managers.DatabaseManager

class ListingCommand : BaseCommand(
    name = "listing",
    description = "Main listing command",
    usage = "/<command> [subcomando]",
    aliases = listOf("l")
) {

    val playerDao = DatabaseManager.getDAO(PlayerDao::class)
    val listingDao = DatabaseManager.getDAO(ListingDao::class)


    override val helpEntries: Array<HelpEntry> = ListingCommands.entries
        .map { it.helpEntry }
        .toTypedArray()
    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {
        return when(args[0].lowercase()) {
            ListingCommands.BALANCE.command -> this.balance(commandSender)
            ListingCommands.GIVE_POINTS.command -> this.givePoints(commandSender, args.drop(1))
            ListingCommands.CREATE.command -> this.create(commandSender, args.drop(1))
            ListingCommands.LIST.command -> this.list(commandSender, args.drop(1))
            else -> this.error(commandSender, args[0])
        }
    }

    private fun list(sender: CommandSender, args: List<String?>): Boolean{
        val player = args.getOrNull(0)?.let { Bukkit.getPlayer(it) } ?: (sender as Player)
        val listings: List<Listing>
        if(sender.hasPermission("economy.op")){
            listings = listingDao.findBySeller(seller = player.uniqueId)
            sender.sendMessage {
                Component.translatable(
                    "info.admin_action"
                )
            }
        }else{
            listings = listingDao.findBySellerActive(seller = player.uniqueId)
        }
        sender.sendMessage {
            Component.translatable(
                "listing.list.response",
                Argument.component("player", player.name())
            )
        }
        for(listing in listings){
            val item = listing.getItem()
            sender.sendMessage {
                listing.id?.let {
                    Component.translatable(
                        "listing.list.line_response",
                        Argument.numeric("id", it),
                        Argument.component("item_name", item.displayName()),
                        Argument.numeric("quantity", listing.quantity),
                        Argument.numeric("price", listing.askingPricePerItem)
                    )
                }!!
            }
        }
        return true;
    }

    private fun create(sender: CommandSender, args: List<String>): Boolean{
        if(args.isEmpty()){
            return this.argumentsMissing(sender)
        }

        val price = args[0].toInt()

        val currentItem = (sender as Player).inventory.itemInMainHand

        if(currentItem.type == Material.AIR){
            sender.sendMessage(Component.translatable(
                "listing.create.error.no_item_in_hand",
            ))
            return true
        }

        val quantity = args.getOrNull(2)?.toIntOrNull() ?: currentItem.amount
        if(quantity > currentItem.amount || quantity > 64){
            sender.sendMessage(
                Component.translatable(
                    "listing.create.error.invalid_amount",
                )
            )
            return true
        }

        val itemBytes = currentItem.serializeAsBytes()

        println(sender.uniqueId.toString())

        val listing = Listing(
            sellerUuid = (sender as Player).uniqueId,
            itemData = itemBytes,
            quantity = quantity,
            askingPricePerItem = price
        )

        val savedListing: Listing = listing.save()
        currentItem.amount -= quantity

        sender.sendMessage(
            Component.translatable(
                "listing.create.response",
                Argument.component(
                    "id",
                    Component.text(savedListing.id.toString()),
                )
            )
        )
        return true;
    }

    private fun givePoints(sender: CommandSender, args: List<String>) : Boolean {
        if(!sender.hasPermission(ListingCommands.GIVE_POINTS.helpEntry.permission)) {
            return this.noPermission(sender)
        }
        if(args.size < 2){
            return this.argumentsMissing(sender)
        }
        val player = Bukkit.getPlayer(args[0]) ?: return this.noPlayer(sender)

        val points = args.getOrNull(1) ?: return this.argumentsMissing(sender)

        val playerEntity = playerDao.findById(player.uniqueId) ?: return false
        playerEntity.points += points.toInt()

        sender.sendMessage {
            Component.translatable(
                "listing.givepoints.response",
                Argument.component("points", Component.text(points)),
                Argument.component("player_name", Component.text(player.name))
            )
        }
        return true;
    }

    private fun balance(sender: CommandSender) : Boolean{
        val points = playerDao.findById((sender as Player).uniqueId)
        sender.sendMessage {
            Component.translatable(
                "listing.balance.response",
                Argument.component("points", Component.text(points.toString()))
            )
        }
        return true
    }



    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()

        when (args.size) {
            1 ->{
                val subcommands = ListingCommands.values()
                    .filter { sender.hasPermission(it.helpEntry.permission) }
                    .map { it.command }
                suggestions.addAll(subcommands)
            }
            2 -> {
                when(args[0].lowercase()) {
                    ListingCommands.REMOVE.command -> suggestions.add("<item id>")
                    ListingCommands.CREATE.command -> suggestions.add("<price>")
                    ListingCommands.GIVE_POINTS.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.REMOVE_POINTS.command ->  suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.LIST.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                }
            }
            3-> {
                if(args[0].equals(ListingCommands.CREATE.command, ignoreCase = true)) {
                    suggestions.add("<amount>")
                }
            }
        }

        return suggestions;
    }



}