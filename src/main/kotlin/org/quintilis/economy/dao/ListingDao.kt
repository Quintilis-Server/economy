package org.quintilis.economy.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import org.quintilis.economy.entities.listings.Listing
import org.quintilis.economy.market.MarketCategory
import org.quintilis.factions.dao.BaseDao
import java.util.UUID

interface ListingDao: BaseDao<Listing, Int> {
    @SqlQuery("SELECT * FROM listings WHERE ")
    fun findAllActiveListing(): List<Listing>

    @SqlQuery("select * from listings where seller_uuid = :seller")
    fun findBySeller(@Bind("seller") seller: UUID): List<Listing>

    @SqlQuery("SELECT * FROM listings WHERE seller_uuid = :seller AND status = 'ACTIVE'")
    fun findBySellerActive(@Bind("seller") seller: UUID): List<Listing>

    @SqlQuery("SELECT * FROM listings WHERE id = :id")
    fun findById(@Bind("id") id: Int): Listing?

    @SqlQuery("SELECT * FROM listings WHERE id = :id FOR UPDATE")
    fun findAndLockById(@Bind("id") id: Int): Listing?

    @SqlQuery("SELECT id FROM listings WHERE seller_uuid = :seller AND status = 'ACTIVE'")
    fun getListingIds(@Bind("seller") seller: UUID): List<Int>

    @Transaction
    @SqlQuery("UPDATE listings SET status = 'CANCELLED' WHERE id = :id AND seller_uuid = :seller RETURNING *")
    fun removeListingById(@Bind("id") id: Int, @Bind("seller")seller: UUID): Listing?

    @SqlQuery("SELECT * FROM listings WHERE status = 'ACTIVE'")
    fun findActive(): List<Listing>

    @SqlQuery("""
        SELECT * FROM listings
        WHERE category = :category
            AND status = 'ACTIVE'
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getListingByCategory(
        @Bind("category") category: MarketCategory,
        @Bind("offset") offset: Int,
        @Bind("limit") limit: Int
    ): List<Listing>

    @SqlQuery("""
        SELECT COUNT(*) FROM listings
        WHERE category = :category
        AND status = 'ACTIVE'
    """)
    fun getTotalByCategory(@Bind("category") category: MarketCategory): Int
}