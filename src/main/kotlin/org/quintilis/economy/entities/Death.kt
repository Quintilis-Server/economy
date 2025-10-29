package org.quintilis.economy.entities

import org.quintilis.economy.entities.annotations.*
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