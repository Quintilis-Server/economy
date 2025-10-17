package org.quintilis.economy.managers

import org.bukkit.configuration.file.FileConfiguration
import org.quintilis.economy.exceptions.ConfigFileNullValueException
import kotlin.text.isNullOrBlank

object ConfigManager {
    private lateinit var config: FileConfiguration

    fun initialize(config: FileConfiguration) {
        this.config = config
    }
    private fun getString(path: String): String{
        val value = this.config.getString("database.$path")
        if(value.isNullOrBlank()){
            throw ConfigFileNullValueException(path)
        }
        return value
    }

    fun getHost(): String{
        return this.getString("host")
    }

    fun getPort(): Int{
        return this.config.getInt("database.port")
    }

    fun getUsername(): String{
        return this.getString("username")
    }

    fun getPassword(): String{
        return this.getString("password")
    }

    fun getDatabaseName(): String{
        return this.getString("dbName")
    }
}