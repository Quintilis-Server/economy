package org.quintilis.economy.commands.market

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class MarketCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry
): Commands {
    BUY(
        command = "buy",
        usage = "/market buy [id]",
        helpEntry = HelpEntry(
            "/market buy [id]",
            "buy",
            "market.buy.command.description",
            "economy.usage"
        )
    ),
    OPEN(
        command = "open",
        usage = "/market open",
        helpEntry = HelpEntry(
            "/market open",
            "open",
            "market.open.command.description",
            "economy.usage"
        )
    )
}