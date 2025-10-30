package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface ItemDao: BaseDao {
    @SqlQuery("SELECT * FROM itens WHERE item_id = :itemId")
    fun findByMineId(@Bind("itemId") itemId: String)
}