package org.quintilis.economy.commands.listing

import org.quintilis.factions.commands.Commands
import org.quintilis.factions.commands.HelpEntry

enum class AdminListingSubCommand(
    override val command: String,
    override val usage: String,
    override val helpEntry: HelpEntry,
    override val subCommands: Array<out Commands>? = null
): Commands {
    CREATE_NPC(
        "create_npc",
        "/listing admin create_npc",
        HelpEntry(
            "listing.admin.create_npc.description",
            "economy.admin"
        )
    )
}