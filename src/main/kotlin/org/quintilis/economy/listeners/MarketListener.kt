package org.quintilis.economy.listeners

import de.oliver.fancynpcs.api.actions.ActionTrigger
import de.oliver.fancynpcs.api.events.NpcInteractEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.quintilis.economy.Economy
import org.quintilis.economy.gui.BaseMarketGUI
import org.quintilis.economy.market.MarketCategory
import org.quintilis.factions.annotations.AutoRegister

@AutoRegister
class MarketListener(val plugin: Economy): Listener {
    @EventHandler
    fun onNpcInteract(event: NpcInteractEvent) {
        if (event.interactionType != ActionTrigger.RIGHT_CLICK) return

        val names = MarketCategory.entries.map{ it.name}
        if(!names.contains(event.npc.data.name)) return

        val category = MarketCategory.valueOf(event.npc.data.name)
        BaseMarketGUI(event.player, category, plugin = plugin).open()
    }
}