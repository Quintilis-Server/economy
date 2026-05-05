package org.quintilis.economy.commands.market

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.Economy
import org.quintilis.economy.gui.BaseMarketGUI
import org.quintilis.economy.market.MarketCategory
import org.quintilis.factions.commands.BaseCommand

class MarketCommand(private val plugin: Economy): BaseCommand(
    name = "market",
    description = "Main buy command",
    usage = "/market <subcommand>",
    aliases = listOf("m"),
    commands = MarketCommands.entries
) {

    private fun open(commandSender: CommandSender, categoryName: String): Boolean{
        val category = MarketCategory.fromString(categoryName) ?: MarketCategory.BLOCKS
        BaseMarketGUI(commandSender as Player, category, plugin).open()
        return true
    }

    override fun commandWrapper(
        commandSender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean {

        return when (args[0].lowercase()) {
            MarketCommands.OPEN.command -> {
                // Se o jogador não digitar a categoria, usamos BLOCKS como padrão
                val categoryName = args.getOrNull(1) ?: "market.blocks"
                open(commandSender, categoryName)
            }
            MarketCommands.BUY.command -> {
                // Exemplo de verificação para o comando buy
                val id = args.getOrNull(1) ?: return run {
                    commandSender.sendMessage("§cPor favor, informe o ID do anúncio.")
                    true
                }
                // lógica de compra...
                true
            }
            else -> true
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val suggestions = mutableListOf<String>()
        if (args.size == 1) {
            suggestions.addAll(MarketCommands.entries.map { it.command })
        }

        if (args.size == 2 && args[0].equals("open", true)) {
            // Retorna as strings das categorias (ex: market.weapons, market.blocks)
            suggestions.addAll(MarketCategory.entries.map { it.displayTag })
        }
        return suggestions
    }
}