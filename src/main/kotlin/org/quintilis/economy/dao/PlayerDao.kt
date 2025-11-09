package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.quintilis.economy.entities.PlayerEntity
import java.util.UUID

interface PlayerDao: BaseDao {
    @SqlQuery("SELECT EXISTS (SELECT 1 FROM players WHERE id = :id);")
    fun isInDatabase(@Bind("id")id: UUID): Boolean

    @SqlQuery("SELECT * FROM players WHERE id = :id")
    fun findById(id: UUID): PlayerEntity?
}