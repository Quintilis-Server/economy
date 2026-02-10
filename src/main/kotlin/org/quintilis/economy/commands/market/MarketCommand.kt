package org.quintilis.economy.commands.market

import org.bukkit.command.CommandSender
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry

class MarketCommand: BaseCommand(
    name = "market",
    description = "Main buy command",
    usage = "/market <subcommand>",
    aliases = listOf("m")
) {
    override val helpEntries: Array<HelpEntry> = MarketCommands.entries
        .map { it.helpEntry }
        .toTypedArray()

    override fun commandWrapper(
        commandSender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean {

        TODO("Not yet implemented")
    }

    override fun onTabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        TODO("Not yet implemented")
    }
}