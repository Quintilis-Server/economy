package org.quintilis.economy.commands.transfer

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.commands.BaseCommand
import org.quintilis.economy.commands.HelpEntry
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.economy.managers.DatabaseManager

class TransferCommand: BaseCommand(
    name = "transfer",
    description = "Transfer points.",
    usage="/transfer [player] [amount]",
    aliases = listOf("t")
) {

    private val playerDao = DatabaseManager.getDAO(PlayerDao::class)

    override val helpEntries: Array<HelpEntry> = TransferCommands.entries
        .map {it.helpEntry}
        .toTypedArray()

    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {
        return when(args[0].lowercase()){
            TransferCommands.LIST.command -> return true
            else -> this.transfer(commandSender, args.drop(1))
        }
    }

    private fun transfer(sender: CommandSender, args: List<String>): Boolean{
        try{

            if(args.size != 2) return this.argumentsMissing(sender)
            val receiverPlayer = Bukkit.getPlayer(args[0]) ?: return this.noPlayer(sender)
            if(receiverPlayer == sender){
                sender.sendMessage{
                    Component.translatable("transfer.error.same_player")
                }
                return true
            }
            if(!receiverPlayer.isOnline){
                sender.sendMessage {
                    Component.translatable("transfer.error.online_player")
                }
                return true
            }
            val senderPlayer = sender as Player
            val amount = args[1].toIntOrNull() ?: return this.argumentsMissing(sender)

            val senderEntity = playerDao.findById(senderPlayer.uniqueId) ?: return false
            val receiverEntity = playerDao.findById(receiverPlayer.uniqueId) ?: return false

            if(senderEntity.points < amount){
                sender.sendMessage {
                    Component.translatable(
                        "transfer.error.no_money"
                    )
                }
                return true;
            }

            val takeTransaction = Transaction(
                playerId = senderPlayer.uniqueId,
                transactionType = TransactionType.TRANSFER_TAKE,
                change = -amount
            ).save<Transaction>()

            senderEntity.points -= amount
            senderEntity.save<PlayerEntity>()

            Transaction(
                playerId = receiverPlayer.uniqueId,
                transactionType = TransactionType.TRANSFER_RECEIVE,
                change = amount,
                parentId = takeTransaction.id,
            ).save<Transaction>()

            receiverEntity.points += amount
            receiverEntity.save<PlayerEntity>()

            sender.sendMessage {
                Component.translatable(
                    "transfer.sender.response",
                    Argument.numeric("points", amount),
                    Argument.string("player", receiverPlayer.name)
                )
            }

            receiverPlayer.sendMessage{
                Component.translatable(
                    "transfer.receiver.response",
                    Argument.numeric("points", amount),
                    Argument.string("player", senderPlayer.name)
                )
            }
            return true
        }catch (ex: Exception){
            sender.sendMessage(Component.text(ex.message!!).color(NamedTextColor.RED))
            return false
        }
    }

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()

        when(args.size){
            1->{
                val subcommands = TransferCommands.entries
                    .filter { sender.hasPermission(it.helpEntry.permission) }
                    .map { it.command }
                suggestions.addAll(subcommands)
            }
            2->{
                suggestions.addAll(Bukkit.getOnlinePlayers()
                    .filter {it.name != sender.name}
                    .map { it.name })
            }
        }

        return suggestions
    }
}