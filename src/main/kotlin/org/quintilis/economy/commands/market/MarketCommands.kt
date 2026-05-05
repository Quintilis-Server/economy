package org.quintilis.economy.commands.market

import org.quintilis.factions.commands.Commands
import org.quintilis.factions.commands.HelpEntry

enum class MarketCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry,
    override val subCommands: Array<out Commands>? = null
): Commands {
    BUY(
        command = "buy",
        usage = "/market buy [id]",
        helpEntry = HelpEntry(
            "market.buy.command.description",
            "economy.usage"
        )
    ),
    OPEN(
        command = "open",
        usage = "/market open",
        helpEntry = HelpEntry(
            "market.open.command.description",
            "economy.usage"
        )
    )
}