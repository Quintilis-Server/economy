package org.quintilis.economy.commands.market

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry
import org.quintilis.economy.commands.transfer.TransferCommands
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.managers.DatabaseManager
import org.quintilis.economy.services.TransactionService

class MarketCommand: BaseCommand(
    name = "market",
    description = "Main buy command",
    usage = "/<command> [subcomando]",
    aliases = listOf("m")
) {
    override val helpEntries: Array<HelpEntry> = MarketCommands.entries.map { it.helpEntry }.toTypedArray()

    private val listingDao = DatabaseManager.getDAO(ListingDao::class)

    override fun commandWrapper(
        commandSender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean {
        return when(args[0]) {
            MarketCommands.OPEN.command -> this.open(commandSender)
            MarketCommands.BUY.command -> this.buy(commandSender, args.drop(1))
            else -> return this.error(commandSender, args[0])
        }
    }

    private fun buy(sender: CommandSender, args: List<String>) :Boolean {
        sender as Player
        val id = args[0].toInt()
        val quantity = args[1].toInt()
        val listing: Listing = listingDao.findById(args[0].toInt()) ?: return this.error(sender, MarketCommands.BUY.command)
        TransactionService.createListingTransaction(listing.getSellerPlayer()!!, sender, quantity, listing)
        return true;
    }

    private fun open(sender: CommandSender) : Boolean {
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val completions = mutableListOf<String>()
        when(args.size) {
            1-> completions.addAll(MarketCommands.entries.map { it.command })
        }
        return completions
    }


}