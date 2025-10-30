package org.quintilis.economy.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.quintilis.economy.dao.PlayerDao
import org.quintilis.economy.entities.Player
import org.quintilis.economy.managers.DatabaseManager
import java.lang.IllegalArgumentException
import java.sql.SQLException
import java.util.logging.Logger

class PlayerJoinListener(private val logger: Logger): Listener {
    private val playerDao: PlayerDao = DatabaseManager.getDAO(PlayerDao::class)
    @EventHandler
    @Throws(IllegalArgumentException::class, IllegalStateException::class, SQLException::class)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId

        if(!this.playerDao.isInDatabase(uuid)){
            logger.info("Player ${player.name} is not in the database")
            val playerEntity = Player(uuid, player.name, 0);
            playerEntity.save();
            logger.info("Player ${player.name} joined successfully")
            return
        }

        logger.info("Player ${player.name} joined successfully")

    }
}