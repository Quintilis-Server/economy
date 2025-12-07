package org.quintilis.economy.commands.market

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class MarketCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry,
): Commands {
    OPEN(
        "open",
        "/market open",
        HelpEntry(
            "/market open",
            "open",
            "market.open.description.command",
            "economy.usage"
        )
    ),
    BUY(
        "buy",
        "/market buy [id] [quantity]",
        HelpEntry(
            "/market buy [id] [quantity]",
            "buy",
            "market.buy.description.command",
            "economy.usage"
        )
    );
}