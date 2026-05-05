package org.quintilis.economy.entities.listings

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.quintilis.economy.market.MarketCategory
import org.quintilis.factions.annotations.Column
import org.quintilis.factions.annotations.PrimaryKey
import org.quintilis.factions.annotations.TableName
import org.quintilis.factions.entities.BaseEntity
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@TableName("listings")
data class Listing(
    @PrimaryKey
    val id: Int? = null,
    @Column("seller_uuid")
    val sellerUuid: UUID,
    @Column("item_data")
    val itemData: ByteArray,
    @Column("quantity")
    var quantity: Int,
    @Column("asking_price_per_item")
    val askingPricePerItem: Int,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("expires_at")
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS),
    @Column("status")
    var status: ListingStatus = ListingStatus.ACTIVE,
    @Column("category")
    var category: MarketCategory
): BaseEntity(){
    fun getItem(): ItemStack {
        val item = ItemStack.deserializeBytes(itemData)
        item.amount *= this.quantity
        return item
    }

    fun getSellerPlayer(): Player? {
        return Bukkit.getPlayer(sellerUuid)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Listing

        if (id != other.id) return false
        if (sellerUuid != other.sellerUuid) return false
        if (!itemData.contentEquals(other.itemData)) return false // Use contentEquals
        if (quantity != other.quantity) return false
        if (askingPricePerItem != other.askingPricePerItem) return false
        if (createdAt != other.createdAt) return false
        if (expiresAt != other.expiresAt) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + sellerUuid.hashCode()
        result = 31 * result + itemData.contentHashCode() // Use contentHashCode
        result = 31 * result + quantity
        result = 31 * result + askingPricePerItem
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + expiresAt.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}