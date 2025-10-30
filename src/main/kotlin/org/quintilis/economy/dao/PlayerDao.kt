package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.quintilis.economy.entities.Player
import java.util.UUID

interface PlayerDao: BaseDao {
    @SqlQuery("SELECT EXISTS (SELECT 1 FROM players WHERE id = :id);")
    fun isInDatabase(@Bind("id")id: UUID): Boolean
}