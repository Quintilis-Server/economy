package org.quintilis.economy.entities

import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName

@TableName("items")
data class Item(
    @PrimaryKey
    val id: Int,
    @Column("item_id")
    val itemId: String
): BaseEntity()
