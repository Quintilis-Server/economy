package org.quintilis.economy.entities.transactions

import net.kyori.adventure.text.Component

enum class TransactionType {
    MARKET_BUY,
    MARKET_SELL,
    PVP_REWARD,
    EVENT_PRIZE,
    ADMIN_GIVE,
    ADMIN_TAKE,
    TRANSFER_TAKE,
    TRANSFER_RECEIVE;

    fun getComponent(): Component{
        return Component.translatable("transaction.type.$name")
    }
}