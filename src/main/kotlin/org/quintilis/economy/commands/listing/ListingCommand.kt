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

    override fun onTabComplete(p0: CommandSender, p2: String, p3: Array<out String>): MutableList<String> {
        val result = mutableListOf<String>()
        return result;
    }



}