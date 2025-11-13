package org.quintilis.economy.commands.transfer

import org.quintilis.economy.commands.Commands
import org.quintilis.economy.commands.HelpEntry

enum class TransferCommands: Commands {
    SEND(
        "send",
        "/transfer send [player] [amount]",
        HelpEntry(
            "/transfer send [player] [amount]",
            "send",
            "transfer.command.description",
            "economy.usage"
        )
    ),
    LIST(
        "list",
        "/transfer list",
        HelpEntry(
            "/transfer list",
            "list",
            "transfer.list.command.description",
            "economy.usage"
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