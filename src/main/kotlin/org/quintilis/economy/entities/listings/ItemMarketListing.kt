package org.quintilis.economy.entities.listings

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import java.util.Date
import java.util.UUID

data class ItemMarketListing(
    @PrimaryKey
    val id: Int,
    @Column("seller_id")
    val sellerId: UUID,
    @Column("item_id")
    val itemId: String,
    @Column("quantity")
    val quantity: Int,
    @Column("asking_price")
    val askingPrice: Int,
    @Column("created_at")
    val createdAt: Date,
    @Column("expires_at")
    val expiresAt: Date,
    @Column("status")
    val status: ListingStatus,
): BaseEntity(){
    fun getItem(): ItemStack? {
        val material = Material.matchMaterial(itemId)
        return if (material != null) {
            ItemStack(material)
        }else{
            null
        }
    }
}