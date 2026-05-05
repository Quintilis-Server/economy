package org.quintilis.economy.commands.transfer

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.economy.services.EconomyServices
import org.quintilis.economy.services.TransactionService
import org.quintilis.factions.commands.BaseCommand
import org.quintilis.factions.services.FactionsServices

class TransferCommand: BaseCommand(
    name = "transfer",
    description = "Transfer points.",
    usage="/transfer [player] [amount]",
    aliases = listOf("t"),
    commands = TransferCommands.entries
) {

    private val playerCache = FactionsServices.playerCache
    private val transactionCache = EconomyServices.transactionCache
    private val listingCache = EconomyServices.listingCache
    private val marketTransactionCache = EconomyServices.marketTransactionCache

//    override val helpEntries: Array<HelpEntry> = TransferCommands.entries
//        .map {it.helpEntry}
//        .toTypedArray()

    override fun commandWrapper(commandSender: CommandSender, label: String, args: Array<out String>): Boolean {
        return when(args[0].lowercase()){
            TransferCommands.LIST.command -> this.list(commandSender)
            else -> this.transfer(commandSender, args.drop(1))
        }
    }

    private fun list(sender: CommandSender): Boolean{
        val transactions = transactionCache.getTransactionsByPlayer((sender as Player).uniqueId)
        val finalTransactions = transactions.mapNotNull { t ->

            // (Lembrete: Isso gera o problema N+1 de performance SQL, mas seguindo sua lógica:)
            val player = playerCache.findById(t.playerId) ?: return@mapNotNull null

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
                    val marketTransaction = marketTransactionCache.findByTransactionId(t.id!!) ?: return@mapNotNull null
                    val listing = listingCache.findById(marketTransaction.listingId) ?: return@mapNotNull null
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
                else -> null
            }
        }
        return true;
    }

    private fun transfer(sender: CommandSender, args: List<String>): Boolean{
        try{

            if(args.size != 2) return this.argumentsMissing(sender)
            val receiverPlayer = Bukkit.getPlayer(args[0]) ?: run {
                this.noPlayer(sender)
                return true
            }
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

            val senderEntity = playerCache.findById(senderPlayer.uniqueId) ?: return false
            val receiverEntity = playerCache.findById(receiverPlayer.uniqueId) ?: return false

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