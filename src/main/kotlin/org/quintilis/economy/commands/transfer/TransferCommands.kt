package org.quintilis.economy.commands.transfer

import org.quintilis.factions.commands.Commands
import org.quintilis.factions.commands.HelpEntry

enum class TransferCommands(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry,
    override val subCommands: Array<out Commands>? = null
    ): Commands {

    SEND(
        "send",
        "/transfer send [player] [amount]",
        HelpEntry(
            "transfer.command.description",
            "economy.usage",
        )
    ),
    LIST(
        "list",
        "/transfer list",
        HelpEntry(
            "transfer.list.command.description",
            "economy.usage"
        )
    );


//    constructor (command: String, usage: String, helpEntry: HelpEntry){
//        this.command = command;
//        this.usage = usage;
//        this.helpEntry = helpEntry;
//    }
}