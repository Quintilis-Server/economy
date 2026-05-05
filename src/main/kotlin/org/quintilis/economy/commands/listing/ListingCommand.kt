package org.quintilis.economy.commands.listing

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.entities.listings.ListingStatus
import org.quintilis.economy.entities.transactions.AdminTransaction
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.economy.market.MarketCategory
import org.quintilis.economy.services.EconomyServices
import org.quintilis.factions.commands.BaseCommand
import org.quintilis.factions.commands.clan.ClanCommands
import org.quintilis.factions.entities.player.PlayerEntity
import org.quintilis.factions.services.FactionsServices

class ListingCommand : BaseCommand(
    name = "listing",
    description = "Main listing command",
    usage = "/<command> [subcomando]",
    aliases = listOf("l"),
    commands = ListingCommands.entries
) {

    val playerCache = FactionsServices.playerCache
    val listingDao = EconomyServices.listingCache


    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {
        val rootCommand = ListingCommands.entries.find {
            it.command.equals(args[0], ignoreCase = true)
        }

        if (rootCommand == null) {
            this.unknownSubCommand(commandSender, args[0])
            return true
        }

        val subArgs = args.drop(1)
        return when(rootCommand) {
            ListingCommands.BALANCE -> this.balance(commandSender)
            // PASSE subArgs DIRETAMENTE, NÃO DÊ DROP(1) DE NOVO
            ListingCommands.GIVE_POINTS -> this.givePoints(commandSender, subArgs)
            ListingCommands.REMOVE_POINTS -> this.removePoints(commandSender, subArgs)
            ListingCommands.CREATE -> this.create(commandSender, subArgs)
            ListingCommands.REMOVE -> this.remove(commandSender, subArgs)
            ListingCommands.LIST -> this.list(commandSender, subArgs)
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

        val detectedCategory = MarketCategory.fromMaterial(currentItem.type)

        var quantity = args.getOrNull(1)?.toIntOrNull()
        if(quantity != null){
            if(quantity > currentItem.amount || quantity > 64 || quantity <= 0){
                sender.sendMessage(
                    Component.translatable(
                        "listing.create.error.invalid_amount",
                    )
                )
                return true
            }
        } else{
            quantity = currentItem.amount
        }

        val itemParaSalvar = currentItem.clone()
        itemParaSalvar.amount = 1

        val itemBytes = itemParaSalvar.serializeAsBytes()

        val listing = Listing(
            sellerUuid = sender.uniqueId,
            itemData = itemBytes,
            quantity = quantity,
            askingPricePerItem = price,
            category = detectedCategory,
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
        val player = Bukkit.getPlayer(args[0]) ?: run {
            this.noPlayer(sender)
            return true
        }

        val points = args.getOrNull(1) ?: return this.argumentsMissing(sender)

        val playerEntity = playerCache.findById(player.uniqueId) ?: return false
        playerEntity.points += points.toInt()
        val savedTransaction = Transaction(
            playerId = player.uniqueId,
            transactionType = TransactionType.ADMIN_GIVE,
            change = points.toInt()
        ).save<Transaction>()
        AdminTransaction(
            transactionId = savedTransaction.id!!,
            adminId = (sender as Player).uniqueId
        ).save<AdminTransaction>()
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
        val player = Bukkit.getPlayer(args[0]) ?: run{
            this.noPlayer(sender)
            return true
        }

        val playerEntity = playerCache.findById(player.uniqueId) ?: return false
        playerEntity.points -= args[1].toInt()

        val savedTransaction = Transaction(
            playerId = player.uniqueId,
            transactionType = TransactionType.ADMIN_TAKE,
            change = args[1].toInt()
        ).save<Transaction>()
        AdminTransaction(
            transactionId = savedTransaction.id!!,
            adminId = (sender as Player).uniqueId
        ).save<AdminTransaction>()

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
        val playerEntity = playerCache.findById((sender as Player).uniqueId) ?: return false
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
                    ListingCommands.CREATE.command -> {
                        // Se o jogador ainda não digitou nada no segundo argumento, mostra o "fantasma"
                        if (args[1].isEmpty()) {
                            suggestions.add("<price>")
                        }
                    }
                    ListingCommands.GIVE_POINTS.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.REMOVE_POINTS.command ->  suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                    ListingCommands.LIST.command -> suggestions.addAll(Bukkit.getOfflinePlayers().mapNotNull { it.name })
                }
            }
            3->{
                if (args[0].equals(ListingCommands.CREATE.command, ignoreCase = true)) {
                    // Se o jogador ainda não digitou nada no terceiro argumento, mostra o "fantasma"
                    if (args[2].isEmpty()) {
                        suggestions.add("<amount>")
                    }
                }
            }
        }

        return suggestions;
    }



}