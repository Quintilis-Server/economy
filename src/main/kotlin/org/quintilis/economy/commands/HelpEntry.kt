package org.quintilis.economy.commands

data class HelpEntry(
    val command: String,
    val descriptionKey: String,
    val permission: String
)
