package org.quintilis.economy.commands.listing

import org.quintilis.factions.commands.Commands
import org.quintilis.factions.commands.HelpEntry

enum class ListingCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry,
    override val subCommands: Array<out Commands>? = null
): Commands {
    REMOVE(
        "remove",
        "/listing remove [id]",
        HelpEntry(
            "listing.remove.command.description",
            "economy.usage"
        )
    ),
    CREATE(
        "create",
        "/listing create [price] [quantity]",
        HelpEntry(
            "listing.create.command.description",
            "economy.usage"
        )
    ),
    BALANCE(
        "balance",
        "/listing balance",
        HelpEntry(
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
            "listing.list.command.description",
            "economy.usage"
        )
    ),
    GIVE_POINTS(
        "givepoints",
        "/listing givepoints [player] [points]",
        HelpEntry(
            "listing.give_points.command.description",
            "economy.op"
        )
    ),
    REMOVE_POINTS(
        "removepoints",
        "/listing removepoints [player] [points]",
        HelpEntry(
            "listing.remove_points.command.description",
            "economy.op"
        )
    ),
    ADMIN(
        "admin",
        "/listing admin [subcommand]",
        HelpEntry(
            "listing.admin.command.description",
            "economy.admin"
        )
    )
}