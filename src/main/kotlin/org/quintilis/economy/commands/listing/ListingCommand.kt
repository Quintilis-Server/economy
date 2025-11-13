package org.quintilis.economy.commands.listing

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.entities.listings.ListingStatus
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
            ListingCommands.REMOVE_POINTS.command -> this.removePoints(commandSender, args.drop(1))
            ListingCommands.CREATE.command -> this.create(commandSender, args.drop(1))
            ListingCommands.REMOVE.command -> this.remove(commandSender, args.drop(1))
            ListingCommands.LIST.command -> this.list(commandSender, args.drop(1))
            else -> this.error(commandSender, args[0])
        }
    }



    private fun list(sender: CommandSender, args: List<String?>): Boolean{
        val player = args.getOrNull(0)?.let { Bukkit.getPlayer(it) } ?: (sender as Player)
        val listings: List<Listing> = listingDao.findBySeller(seller = player.uniqueId)

        sender.sendMessage {
            Component.translatable(
                "listing.list.response",
                Argument.component("player", player.name()),
                Argument.numeric("quantity", listings.size)
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
                        Argument.numeric("price", listing.askingPricePerItem),
                        Argument.component("status", listing.status.getComponent())
                    )
                }!!
            }
        }
        return true;
    }

    private fun remove(sender: CommandSender, args: List<String>): Boolean{
        if(args.isEmpty()) return this.argumentsMissing(sender)

        val listingId = args[0].toIntOrNull() ?: return this.argumentsMissing(sender)

        val player = sender as Player

        val listing = listingDao.findById(listingId)

        if(listing == null){
            sender.sendMessage {
                Component.translatable(
                    "listing.remove.error.not_found",
                    Argument.numeric("id", listingId)
                )
            }
            return true
        }
        if(listing.status != ListingStatus.ACTIVE){
            sender.sendMessage {
                Component.translatable(
                    "listing.remove.error.listing_no_active",
                    Argument.component("state", listing.status.getComponent())

                )
            }
            return true
        }

        // 1. O DAO agora faz a checagem E a atualização de uma só vez
        val cancelledListing = listingDao.removeListingById(listingId, player.uniqueId)

        if (cancelledListing == null) {
            sender.sendMessage(
                Component.translatable("listing.remove.error.not_found")
            )
            return true
        }

        val itemStack = cancelledListing.getItem()

        val itemsSobrantes = player.inventory.addItem(itemStack)
        for (item in itemsSobrantes.values) {
            player.world.dropItem(player.location, item)
        }

        sender.sendMessage(
            Component.translatable(
                "listing.remove.response",
                Argument.component("id", Component.text(cancelledListing.id.toString()))
            )
        )
        return true
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
            sellerUuid = sender.uniqueId,
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

        playerEntity.save<PlayerEntity>()

        sender.sendMessage {
            Component.translatable(
                "listing.give_points.response",
                Argument.component("points", Component.text(points)),
                Argument.component("player_name", Component.text(player.name))
            )
        }
        return true;
    }

    private fun removePoints(sender: CommandSender, args: List<String>): Boolean{
        if(sender.hasPermission(ListingCommands.REMOVE_POINTS.helpEntry.permission)){
            return this.noPermission(sender)
        }
        if(args.size> 2){
            return this.argumentsMissing(sender)
        }
        val player = Bukkit.getPlayer(args[0]) ?: return this.noPlayer(sender)

        val playerEntity = playerDao.findById(player.uniqueId) ?: return false
        playerEntity.points -= args[1].toInt()

        val savedEntity:PlayerEntity = playerEntity.save()

        sender.sendMessage {
            Component.translatable(
                "listing.remove_points.response",
                Argument.numeric("points", savedEntity.points),
                Argument.string("player_name", player.name)
            )
        }
        return true
    }

    private fun balance(sender: CommandSender) : Boolean{
        val playerEntity = playerDao.findById((sender as Player).uniqueId) ?: return false
        sender.sendMessage {
            Component.translatable(
                "listing.balance.response",
                Argument.numeric("points", playerEntity.points)
            )
        }
        return true
    }



    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()

        when (args.size) {
            1 ->{
                val subcommands = ListingCommands.entries
                    .filter { sender.hasPermission(it.helpEntry.permission) }
                    .map { it.command }
                suggestions.addAll(subcommands)
            }
            2 -> {
                when(args[0].lowercase()) {
                    ListingCommands.REMOVE.command -> suggestions.addAll(listingDao.getListingIds((sender as Player).uniqueId).map { it.toString() })
//                    ListingCommands.CREATE.command -> suggestions.add
                    ListingCommands.GIVE_POINTS.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.REMOVE_POINTS.command ->  suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.LIST.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                }
            }
//            3-> {
//                if(args[0].equals(ListingCommands.CREATE.command, ignoreCase = true)) {
////                    suggestions.add("<amount>")
//                }
//            }
        }

        return suggestions;
    }



}