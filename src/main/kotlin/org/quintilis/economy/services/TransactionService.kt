package org.quintilis.economy.services

import org.bukkit.entity.Player
import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.entities.transactions.MarketTransaction
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType

class TransactionService{

    companion object {
        fun createListingTransaction(seller: Player, buyer: Player, quantity: Int, listing: Listing) {
            val change = quantity * listing.askingPricePerItem
            val buyTransaction = Transaction(
                playerId = buyer.uniqueId,
                transactionType = TransactionType.MARKET_BUY,
                change = -change,
            ).save<Transaction>()
            MarketTransaction(
                transactionId = buyTransaction.id!!,
                listing.id!!,
                quantity = quantity,
            ).save<MarketTransaction>()

            Transaction(
                playerId = seller.uniqueId,
                transactionType = TransactionType.MARKET_SELL,
                change = change,
                parentId = buyTransaction.id,
            ).save<Transaction>()
            listing.quantity -= quantity

            listing.save<BaseEntity>()
        }

        fun createTransferTransaction(sender: PlayerEntity, receiver: PlayerEntity, change: Int){
            val takeTransaction = Transaction(
                playerId = sender.id,
                transactionType = TransactionType.TRANSFER_TAKE,
                change = -change,
            ).save<Transaction>()

            Transaction(
                playerId = receiver.id,
                transactionType = TransactionType.TRANSFER_RECEIVE,
                change = change,
                parentId = takeTransaction.id
            ).save<Transaction>()

            sender.points -= change
            receiver.points += change
            sender.save<BaseEntity>()
            receiver.save<BaseEntity>()
        }
    }
}