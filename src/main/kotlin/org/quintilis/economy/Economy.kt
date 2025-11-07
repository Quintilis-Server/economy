package org.quintilis.economy

import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationStore
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.commands.listing.ListingCommand
import org.quintilis.economy.listeners.PlayerJoinListener
import org.quintilis.economy.managers.ConfigManager
import org.quintilis.economy.managers.DatabaseManager
import java.sql.Connection
import java.sql.SQLException
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

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

        logger.info("Registering translations manually...")
        this.registerTranslations()

        //add listener
        this.server.pluginManager.registerEvents(PlayerJoinListener(this.logger), this)

        //commands
        val listingCommand = ListingCommand();
        this.server.commandMap.register(listingCommand.name, "economy", listingCommand)



    }

    private fun registerTranslations() {

        val translationKey = Key.key("economy", "translations")

        val store = MiniMessageTranslationStore.create(translationKey)

        val english = Locale.US
        val portuguese = Locale.forLanguageTag("pt-BR")

        val bundlePath = "translations.economy"

        try {
            val bundleEN = ResourceBundle.getBundle(bundlePath, english)
            val bundlePT = ResourceBundle.getBundle(bundlePath, portuguese)

            store.registerAll(english, bundleEN, false)
            store.registerAll(portuguese, bundlePT, false)

        } catch (e: MissingResourceException) {
            logger.warning("NÃO FOI POSSÍVEL ENCONTRAR OS ARQUIVOS DE TRADUÇÃO NO JAR!")
            logger.warning("Verifique o caminho: $bundlePath")
            return
        }

        GlobalTranslator.translator().addSource(store)

        logger.info("Translation sources (en, pt_BR) registered successfully.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
