package org.quintilis.economy.commands.listing

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class ListingCommands: Commands {
    MARKET(
        "remove",
        "/listing remove [id]",
        HelpEntry(
            "/listing remove [id]",
            "listing.remove.command.description",
            "economy.usage"
        )
    ),
    SELL(
        "create",
        "/listing create [price] [quantity]",
        HelpEntry(
            "/listing create [price] [quantity]",
            "listing.create.command.description",
            "economy.usage"
        )
    ),
    BALANCE(
        "help",
        "/listing help [page]",
        HelpEntry(
            "/listing help [page]",
            "help.command.listing.description",
            "economy.help"
        )
    );

    override val command: String
    override val usage: String
    override val helpEntry: HelpEntry

    constructor (command: String, usage: String, helpEntry: HelpEntry){
        this.command = command;
        this.usage = usage;
        this.helpEntry = helpEntry;
    }
}