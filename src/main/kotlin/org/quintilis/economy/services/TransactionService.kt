package org.quintilis.economy.services

import org.bukkit.Chunk
import org.quintilis.economy.dao.ListingDao
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.entities.listings.ListingStatus
import org.quintilis.economy.entities.transactions.Transaction
import org.quintilis.economy.entities.transactions.TransactionType
import org.quintilis.factions.entities.BaseEntity
import org.quintilis.factions.entities.player.PlayerEntity
import org.quintilis.factions.extensions.sendTranslatable
import org.quintilis.factions.managers.DatabaseManager
import org.quintilis.factions.services.FactionsServices

class TransactionService {
    sealed class TransactionResult {
        object NOT_FOUND : TransactionResult()
        object ALREADY_SOLD : TransactionResult()
        object INSUFFICIENT_FUNDS : TransactionResult()
        object SELLER_IS_BUYER : TransactionResult()
        object INVENTORY_FULL : TransactionResult()
        object GENERIC_ERROR : TransactionResult()
        data class SUCCESS_PARTIAL(val listing: Listing, val finalQuantity: Int) : TransactionResult()
    }
    companion object{
        fun buyPartialListing(
            buyer: PlayerEntity,
            listingId: Int,
            requestedAmount: Int
        ): TransactionResult {
            //fazer dentro da transaction para que duas pessoas não comprem do mesmo anúncio
            return try {

                DatabaseManager.jdbi.inTransaction<TransactionResult, Exception> { handle ->
                    val dao = handle.attach(ListingDao::class.java)

                    val listing = dao.findByIdForUpdate(listingId)
                        ?: return@inTransaction TransactionResult.NOT_FOUND
                    //                val listing = listingCache.findById(listingId) ?: return@inTransaction

                    if (listing.status != ListingStatus.ACTIVE) {
                        return@inTransaction TransactionResult.ALREADY_SOLD
                    }

                    if (listing.sellerUuid == buyer.id) {
                        return@inTransaction TransactionResult.SELLER_IS_BUYER
                    }

                    val actualAmount = if (requestedAmount > listing.quantity) listing.quantity else requestedAmount
                    val totalPrice = listing.askingPricePerItem * actualAmount

                    if (buyer.points < totalPrice) return@inTransaction TransactionResult.INSUFFICIENT_FUNDS

                    buyer.points -= totalPrice
                    buyer.save<BaseEntity>()

                    val sellerEntity = FactionsServices.playerCache.findById(listing.sellerUuid)
                    sellerEntity?.let {
                        it.points += totalPrice
                        it.save<BaseEntity>()
                    }

                    Transaction(
                        playerId = buyer.id,
                        transactionType = TransactionType.MARKET_BUY,
                        change = -totalPrice,
                    ).save<Transaction>()

                    Transaction(
                        playerId = sellerEntity?.id!!,
                        transactionType = TransactionType.MARKET_SELL,
                        change = totalPrice,
                    ).save<Transaction>()

                    // 5. Atualiza o Anúncio
                    listing.quantity -= actualAmount
                    if (listing.quantity <= 0) {
                        listing.status = ListingStatus.SOLD
                    }

                    listing.save<BaseEntity>()

                    buyer.getPlayer()?.sendTranslatable("market.")

                    // Retornamos a quantidade que ele realmente conseguiu comprar
                    TransactionResult.SUCCESS_PARTIAL(listing, actualAmount)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                TransactionResult.GENERIC_ERROR
            }
        }


        fun createClaimTransaction(leader: PlayerEntity, chunk: Chunk) {

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