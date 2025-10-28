package org.quintilis.economy.entites

import org.quintilis.economy.entites.annotations.*
import java.util.Date
import java.util.UUID

@TableName("deaths")
data class Death(
    @PrimaryKey
    val id: Int,
    @Column("player_id")
    val playerId: UUID,
    @Column("killer_id")
    val killerId: UUID,
    @Column("created_at")
    val createdAt: Date
) : BaseEntity() {
}