package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.quintilis.economy.entities.listings.Listing
import java.util.UUID

interface ListingDao: BaseDao {
    @SqlQuery("select * from listings where seller_uuid = :seller")
    fun findBySeller(@Bind("seller") seller: UUID): List<Listing>

    @SqlQuery("SELECT * FROM listings WHERE seller_uuid = :seller AND status = 'ACTIVE'")
    fun findBySellerActive(@Bind("seller") seller: UUID): List<Listing>
}