package org.quintilis.economy.services

import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.PlayerEntity
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.entities.listings.ListingStatus
import org.quintilis.economy.entities.transactions.MarketTransaction
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.economy.exceptions.InvalidQuantity
import org.quintilis.economy.exceptions.ListingNotActive
import org.quintilis.economy.exceptions.ListingNotFound
import org.quintilis.economy.exceptions.NotEnoughPoints
import org.quintilis.economy.exceptions.PlayerNotFound
import org.quintilis.economy.managers.DatabaseManager

class TransactionService {
    companion object{
        fun createListingTransaction(
            seller: PlayerEntity,
            buyer: PlayerEntity,
            listingId: Int,
            quantity: Int
        ): Unit {
            lateinit var boughtListing: Listing
            //fazer dentro da transaction para que duas pessoas não comprem do mesmo anúncio
            DatabaseManager.jdbi.inTransaction<Unit, Exception> { handle ->

                val listingDao = handle.attach(ListingDao::class.java)
                val listing = listingDao.findAndLockById(listingId)

                if(listing == null){
                    handle.rollback()
                    throw ListingNotFound(listingId)
                }

                if(listing.status != ListingStatus.ACTIVE){
                    handle.rollback()
                    throw ListingNotActive(listingId)
                }

                if(listing.quantity < quantity){
                    handle.rollback()
                    throw InvalidQuantity(quantity, listingId)
                }

                val value = listing.askingPricePerItem * quantity

                if(buyer.points > value){
                    handle.rollback()
                    throw NotEnoughPoints()
                }

                //salva as transações
                val buyTransaction = Transaction(
                    playerId = buyer.id,
                    transactionType = TransactionType.MARKET_BUY,
                    change = -value
                ).save<Transaction>()

                MarketTransaction(
                    transactionId = buyTransaction.id!!,
                    quantity = quantity,
                    listingId = listing.id!!
                ).save<MarketTransaction>()

                Transaction(
                    playerId = seller.id,
                    transactionType = TransactionType.MARKET_SELL,
                    change = value,
                    parentId = buyTransaction.id
                ).save<Transaction>()

                //checa se foi comprado todos os itens do anúncio
                if(quantity == listing.quantity){
                    //se sim ele deixa o anúncio como vendido
                    listing.status = ListingStatus.SOLD
                }else{
                    //se não ele subtrai da quantidade do anúncio
                    listing.quantity -= quantity
                }
                val savedListing = listing.save<Listing>()

                //muda os pontos dos jogadores
                seller.points +=value
                buyer.points -=value

                seller.save<BaseEntity>()
                buyer.save<BaseEntity>()

                boughtListing = savedListing
            }
        }


        fun createTransferTransaction(sender: PlayerEntity, receiver: PlayerEntity, quantity: Int){
            DatabaseManager.jdbi.inTransaction<Unit, Exception> {

                val takeTransaction = Transaction(
                    playerId = sender.id,
                    transactionType = TransactionType.TRANSFER_TAKE,
                    change = -quantity,
                ).save<Transaction>()

                Transaction(
                    playerId = receiver.id,
                    transactionType = TransactionType.TRANSFER_RECEIVE,
                    change = quantity,
                    parentId = takeTransaction.id,
                ).save<Transaction>()

                sender.points -= quantity
                receiver.points += quantity

                sender.save<BaseEntity>()
                receiver.save<BaseEntity>()
            }
        }
    }
}