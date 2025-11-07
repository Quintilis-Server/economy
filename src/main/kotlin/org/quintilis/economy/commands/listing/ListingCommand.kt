package org.quintilis.economy.commands.listing

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry

class ListingCommand : BaseCommand(
    name = "listing",
    description = "Main listing command",
    usage = "/<command> [subcomando]",
    aliases = listOf("l")
) {
    override val helpEntries: Array<HelpEntry> = ListingCommands.values()
        .map { it.helpEntry }
        .toTypedArray()
    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {


        return true;
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
                    ListingCommands.MARKET.command -> suggestions.add("<item id>")
                    ListingCommands.SELL.command -> suggestions.add("<price>")
                }
            }
            3-> {
                if(args[0].equals(ListingCommands.SELL.command, ignoreCase = true)) {
                    suggestions.add("<amount>")
                }
            }
        }

        return suggestions;
    }



}