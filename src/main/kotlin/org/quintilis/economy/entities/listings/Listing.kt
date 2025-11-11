package org.quintilis.economy.entities.listings

import org.bukkit.inventory.ItemStack
import org.quintilis.economy.entities.BaseEntity
import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName
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
    val quantity: Int,
    @Column("asking_price_per_item")
    val askingPricePerItem: Int,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("expires_at")
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS),
    @Column("status")
    val status: ListingStatus = ListingStatus.ACTIVE,
): BaseEntity(){
    fun getItem(): ItemStack {
        return ItemStack.deserializeBytes(itemData)
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