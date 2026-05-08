package org.quintilis.economy

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.quintilis.economy.commands.listing.ListingCommand
import org.quintilis.economy.commands.transfer.TransferCommand
import org.quintilis.economy.managers.ConfigManager
import org.quintilis.economy.services.EconomyServices
import org.quintilis.factions.annotations.AutoRegister
import org.quintilis.factions.commands.BaseCommand
import org.quintilis.factions.managers.DatabaseManager
import org.quintilis.factions.managers.RedisManager
import org.quintilis.factions.managers.TranslationManager
import org.quintilis.factions.placeholders.FactionsLangExpansion
import org.quintilis.factions.services.FactionsServices
import org.quintilis.factions.util.ClassScanner
import java.sql.Connection

class Economy : JavaPlugin() {
    lateinit var connection: Connection;
    override fun onEnable() {
        this.saveDefaultConfig()

        ConfigManager.initialize(this.config, plugin = this)

        try {
            logger.info("Conectando ao banco de dados PostgreSQL...")
            DatabaseManager.connect(this.logger, ConfigManager)
            logger.info("Conexão com o banco de dados estabelecida com sucesso!")
        } catch (e: Exception) {
            logger.severe("FALHA AO CONECTAR COM O BANCO DE DADOS! Desabilitando o plugin...")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
            return
        }

        try{
            logger.info("Conectando ao banco de dados Redis...")
            RedisManager.connect(ConfigManager)
            logger.info("Conexão com o banco de dados REDIS estabelecida com sucesso!")
        }catch (e: Exception){
            logger.severe("FALHA AO CONECTAR COM O REDIS! Desabilitando o plugin...")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
            return
        }
        FactionsServices.init(this)
        EconomyServices.init(this)

        this.registerEvents()

        this.registerCommands()

        this.registerTranslations()



    }

    private fun registerCommands(){
        //, MarketCommand(this)
        val commands: List<BaseCommand> = listOf(ListingCommand(), TransferCommand())
        this.server.commandMap.registerAll("economy", commands)
    }

    private fun registerEvents(){
        val classes:List<Class<Listener>> = ClassScanner.findClasses<Listener, AutoRegister>(
            this,
            "org.quintilis.economy",
        )

        classes.forEach { clazz ->
            val listener = try {
                // Agora ele procura pelo construtor genérico JavaPlugin
                clazz.getConstructor(JavaPlugin::class.java).newInstance(this)
            } catch (e: NoSuchMethodException) {
                try {
                    // Tenta achar o construtor exato (Factions) como fallback
                    clazz.getConstructor(Economy::class.java).newInstance(this)
                } catch (e2: NoSuchMethodException) {
                    try {
                        // Se falhar os dois, tenta o construtor vazio ()
                        clazz.getConstructor().newInstance()
                    } catch (e3: Exception) {
                        logger.severe("Não foi possível registrar o listener ${clazz.simpleName}. Verifique os construtores.")
                        return@forEach
                    }
                }
            }

            server.pluginManager.registerEvents(listener, this)
            logger.info("Listener registrado: ${clazz.simpleName}")
        }
    }

    private fun registerTranslations() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            FactionsLangExpansion(
                "economy",
                "Quintilis",
                "1.0-SNAPSHOT",
            ).register()
            logger.info("PlaceholderAPI conectado! Traduções dinâmicas ativadas.")
        }
        TranslationManager.registerTranslations(this, "economy")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
