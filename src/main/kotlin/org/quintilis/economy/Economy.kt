package org.quintilis.economy

import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.listeners.PlayerJoinListener
import org.quintilis.economy.managers.ConfigManager
import org.quintilis.economy.managers.DatabaseManager
import java.sql.Connection
import java.sql.SQLException

class Economy : JavaPlugin() {
    lateinit var connection: Connection;
    override fun onEnable() {
        logger.info("Initializing Economy config")
        this.saveDefaultConfig()
        ConfigManager.initialize(this.config)
        logger.info("Connecting Database PostgreSQL")
        try{
            DatabaseManager.connect();
            this.connection = DatabaseManager.getConnection();
            logger.info("Connected to database")
        }catch(e: SQLException){
            logger.severe("Database connection error: ${e.message}")
            server.pluginManager.disablePlugin(this)
        }

        //add listener
        this.server.pluginManager.registerEvents(PlayerJoinListener(this.logger), this)

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
