package org.quintilis.economy.dao

import org.quintilis.economy.entities.transactions.AdminTransaction
import org.quintilis.factions.dao.BaseDao

interface AdminTransactionDao: BaseDao<AdminTransaction, Int> {
}