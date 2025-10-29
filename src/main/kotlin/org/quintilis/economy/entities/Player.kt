package org.quintilis.economy.entities

import org.quintilis.economy.entities.annotations.Column
import org.quintilis.economy.entities.annotations.PrimaryKey
import org.quintilis.economy.entities.annotations.TableName
import java.util.UUID

@TableName("players")
data class Player(
    @PrimaryKey
    val id: UUID,
    @Column("name")
    val name: String,
    @Column("points")
    val points: Int
): BaseEntity(){
}