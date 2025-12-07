package org.quintilis.economy.commands

interface Commands {
    val command: String
    val usage: String
    val helpEntry: HelpEntry

}