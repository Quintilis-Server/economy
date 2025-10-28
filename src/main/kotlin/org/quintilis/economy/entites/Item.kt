package org.quintilis.economy.entites

import org.quintilis.economy.entites.annotations.Column
import org.quintilis.economy.entites.annotations.PrimaryKey
import org.quintilis.economy.entites.annotations.TableName

@TableName("items")
data class Item(
    @PrimaryKey
    val id: Int,
    @Column("item_id")
    val itemId: String
): BaseEntity()
