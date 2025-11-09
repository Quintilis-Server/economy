package org.quintilis.economy.commands.listing

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class ListingCommands: Commands {
    REMOVE(
        "remove",
        "/listing remove [id]",
        HelpEntry(
            "/listing remove [id]",
            "listing.remove.command.description",
            "economy.usage"
        )
    ),
    CREATE(
        "create",
        "/listing create [price] [quantity]",
        HelpEntry(
            "/listing create [price] [quantity]",
            "listing.create.command.description",
            "economy.usage"
        )
    ),
    BALANCE(
        "balance",
        "/listing balance",
        HelpEntry(
            "/listing balance",
            "listing.balance.command.description",
            ""
        )
    ),
    MARKET(
        "market",
        "/listing market",
        HelpEntry(
            "/listing market",
            "listing.market.command.description",
            "economy.usage"
        )
    ),
    LIST(
        "list",
        "/listing list",
        HelpEntry(
            "/listing list",
            "listing.list.command.description",
            "economy.usage"
        )
    ),
    GIVE_POINTS(
        "givepoints",
        "/listing givepoints <player> <points>",
        HelpEntry(
            "/listing givepoints <player> <points>",
            "listing.givepoints.command.description",
            "economy.op"
        )
    ),
    REMOVE_POINTS(
        "removepoints",
        "/listing removepoints <player> <points>",
        HelpEntry(
            "/listing removepoints <player> <points>",
            "listing.removepoints.command.description",
            "economy.op"
        )
    )
    ;

    override val command: String
    override val usage: String
    override val helpEntry: HelpEntry

    constructor (command: String, usage: String, helpEntry: HelpEntry){
        this.command = command;
        this.usage = usage;
        this.helpEntry = helpEntry;
    }
}