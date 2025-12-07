package org.quintilis.economy.commands.listing

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class ListingCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry
): Commands {
    REMOVE(
        "remove",
        "/listing remove [id]",
        HelpEntry(
            "/listing remove [id]",
            "remove",
            "listing.remove.command.description",
            "economy.usage"
        )
    ),
    CREATE(
        "create",
        "/listing create [price] [quantity]",
        HelpEntry(
            "/listing create [price] [quantity]",
            "create",
            "listing.create.command.description",
            "economy.usage"
        )
    ),
    BALANCE(
        "balance",
        "/listing balance",
        HelpEntry(
            "/listing balance",
            "balance",
            "listing.balance.command.description",
            "economy.usage"
        )
    ),
//    MARKET(
//        "market",
//        "/listing market",
//        HelpEntry(
//            "/listing market",
//            "market",
//            "listing.market.command.description",
//            "economy.usage"
//        )
//    ),
    LIST(
        "list",
        "/listing list [player]",
        HelpEntry(
            "/listing list [player]",
            "list",
            "listing.list.command.description",
            "economy.usage"
        )
    ),
    GIVE_POINTS(
        "givepoints",
        "/listing givepoints [player] [points]",
        HelpEntry(
            "/listing givepoints [player] [points]",
            "givepoints",
            "listing.give_points.command.description",
            "economy.op"
        )
    ),
    REMOVE_POINTS(
        "removepoints",
        "/listing removepoints [player] [points]",
        HelpEntry(
            "/listing removepoints [player] [points]",
            "removepoints",
            "listing.remove_points.command.description",
            "economy.op"
        )
    )
}