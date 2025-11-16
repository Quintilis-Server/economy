package org.quintilis.economy.entities.listings

import net.kyori.adventure.text.Component

enum class ListingStatus {
    ACTIVE,
    SOLD,
    EXPIRED,
    CANCELLED;

    fun getComponent(): Component{
        return Component.translatable("listing.status.${this.name}")
    }
}