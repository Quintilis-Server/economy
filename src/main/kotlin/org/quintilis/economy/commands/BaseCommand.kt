package org.quintilis.economy.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

abstract class BaseCommand(
    name: String,
    description: String,
    usage: String,
    aliases: List<String>,
//    val commands: Commands
) : Command(name, description, usage, aliases) {

    protected abstract val helpEntries: Array<HelpEntry>;

    abstract fun commandWrapper(
        commandSender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean

    abstract fun onTabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>
    ): MutableList<String>

    private val pageSize = 5;
//    private val mm = MiniMessage.miniMessage()


    override fun execute(commandSender: CommandSender, label: String, args: Array<String>): Boolean {
        if(commandSender !is Player) {
            commandSender.sendMessage(Component.translatable("error.is_not_player"))
            return true;
        }
        if(args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
            val helpArgs = if (args.isNotEmpty()) {
                args.copyOfRange(1, args.size).toList()
            } else {
                emptyList()
            }

            this.help(commandSender, helpArgs)
            return true
        }

        return this.commandWrapper(commandSender, label, args);
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        val input = args.lastOrNull() ?: ""

        val suggestions: MutableList<String> = this.onTabComplete(sender, alias, args)
        if(args.size == 1) {
            suggestions.add("help")
        }
        val completions = mutableListOf<String>()
        StringUtil.copyPartialMatches(
            input,
            suggestions.distinct(),
            completions
        )
        return completions;
    }

    protected fun help(sender: CommandSender, args: List<String>) {
        val accessibleCommands = this.helpEntries.filter { sender.hasPermission(it.permission) }

        val totalPages = max(1, ceil(accessibleCommands.size.toDouble() / pageSize).toInt())
        val page = args.getOrNull(0)?.toIntOrNull() ?: 1

        if(page !in 1..totalPages){
            sender.sendMessage(
                Component.translatable(
                    "error.invalid_page",
                    Argument.component("total_pages",  Component.text(totalPages))
                )
            )

            return
        }

        sender.sendMessage(
            Component.translatable(
                "help.header",
                Argument.component("page", Component.text(page)),
                Argument.component("total_pages",Component.text(totalPages))
            )
        )

        val startIndex = (page - 1) * pageSize
        val endIndex = min(startIndex + pageSize, accessibleCommands.size)
        val pageEntries = accessibleCommands.subList(startIndex, endIndex)

        for(entry in pageEntries){
            val descriptionComponent = Component.translatable(entry.descriptionKey)

            val descriptionArg = Argument.component("description", descriptionComponent)

            val commandArg = Argument.component("command", Component.text(entry.command))

            val lineComponent = Component.translatable(
                "help.command.format",
                commandArg,
                descriptionArg,
            )
            sender.sendMessage(lineComponent)
        }
        sender.sendMessage(
            Component.translatable(
                "help.footer",
                Argument.component("command", Component.text(this.name))
            )
        )
    }
}