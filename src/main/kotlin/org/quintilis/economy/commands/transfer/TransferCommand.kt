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
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.dao.MarketTransactionDao
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.dao.TransactionDao
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.economy.managers.DatabaseManager
import org.quintilis.economy.services.TransactionService

class TransferCommand: BaseCommand(
    name = "transfer",
    description = "Transfer points.",
    usage="/transfer [player] [amount]",
    aliases = listOf("t")
) {

    private val playerDao = DatabaseManager.getDAO(PlayerDao::class)
    private val transactionDao = DatabaseManager.getDAO(TransactionDao::class)
    private val listingDao = DatabaseManager.getDAO(ListingDao::class)
    private val marketTransactionDao = DatabaseManager.getDAO(MarketTransactionDao::class)

    override val helpEntries: Array<HelpEntry> = TransferCommands.entries
        .map {it.helpEntry}
        .toTypedArray()

    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {
        return when(args[0].lowercase()){
            TransferCommands.LIST.command -> this.list(commandSender)
            else -> this.transfer(commandSender, args.drop(1))
        }
    }

    private fun list(sender: CommandSender): Boolean{
        val transactions = transactionDao.getTransactionsByPlayer((sender as Player).uniqueId)
        val finalTransactions = transactions.mapNotNull { t ->

            // (Lembrete: Isso gera o problema N+1 de performance SQL, mas seguindo sua lógica:)
            val player = playerDao.findById(t.playerId) ?: return@mapNotNull null

            when (t.transactionType) {
                // CASO 1: Transferências
                TransactionType.TRANSFER_TAKE, TransactionType.TRANSFER_RECEIVE -> {
                    Component.translatable(
                        "transfer.list.${t.transactionType.name.lowercase()}_line_response", // ex: transfer_take_line_response
                        Argument.numeric("id", t.id!!),
                        Argument.component("type", t.transactionType.getComponent()),
                        Argument.string("player_name", player.name),
                        Argument.numeric("change", t.change)
                    )
                }

                // CASO 2: Mercado
                TransactionType.MARKET_BUY, TransactionType.MARKET_SELL -> {
                    val marketTransaction = marketTransactionDao.findByTransactionId(t.id!!) ?: return@mapNotNull null
                    val listing = listingDao.findById(marketTransaction.listingId) ?: return@mapNotNull null
                    Component.translatable(
                        "market.list.${t.transactionType.name.lowercase()}_line_response",
                        Argument.numeric("id", t.id),
                        Argument.numeric("listing_id", marketTransaction.listingId),
                        Argument.string("player_name", Bukkit.getPlayer(listing.sellerUuid)!!.name),
                        Argument.component("item_name", listing.getItem().displayName()),
                        Argument.numeric("quantity", marketTransaction.quantity),
                        Argument.numeric("price", listing.askingPricePerItem),
                    )
                }

                // Outros tipos (ignorar)
                else -> null
            }
        }
//        for(transaction in finalTransactions){
//            sender.sendCommand {
//
//            }
//        }
        return true;
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

            TransactionService.createTransferTransaction(senderEntity, receiverEntity, amount)

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
            ex.printStackTrace()
//            sender.sendMessage(Component.text(ex.message!!).color(NamedTextColor.RED))
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