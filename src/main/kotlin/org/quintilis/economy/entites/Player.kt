package org.quintilis.economy.entites

import org.quintilis.economy.entites.annotations.Column
import org.quintilis.economy.entites.annotations.PrimaryKey
import org.quintilis.economy.entites.annotations.TableName
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