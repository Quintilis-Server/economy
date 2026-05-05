package org.quintilis.economy.market

import org.bukkit.Material

enum class MarketCategory(val displayTag: String, val icon: Material) {
    WEAPONS("market.weapons", Material.NETHERITE_SWORD),
    ARMOR("market.armor", Material.DIAMOND_CHESTPLATE),
    TOOLS("market.tools", Material.DIAMOND_HOE),
    FOOD("market.food", Material.COOKED_BEEF),
    ORES("market.ores", Material.GOLD_ORE),
    BLOCKS("market.blocks", Material.OAK_WOOD),
    OTHER("market.other", Material.POTION);

    companion object {
        fun fromString(name: String): MarketCategory? =
            entries.firstOrNull { it.displayTag.equals(name, true) }

        fun fromMaterial(material: Material): MarketCategory {
            val name = material.name

            return when {
                material.isEdible || name.contains("POTION") || name.contains("BOTTLE") -> FOOD

                name.contains("HELMET") || name.contains("CHESTPLATE") ||
                name.contains("LEGGINS") || name.contains("BOOTS") || name == "ELYTRA" -> ARMOR

                name.endsWith("_SWORD") || name.endsWith("_AXE") ||
                name == "BOW" || name == "CROSSBOW" || name == "TRIDENT" -> WEAPONS

                name.endsWith("_PICKAXE") || name.endsWith("_SHOVEL") ||
                name.endsWith("_HOE") || name == "SHEARS" || name == "FISHING_ROD" ||
                name == "FLINT_AND_STEEL" -> TOOLS

                name.endsWith("_ORE") || name.endsWith("_INGOT") || name.endsWith("_NUGGET") ||
                name == "DIAMOND" || name == "EMERALD" || name == "COAL" || name == "LAPIS_LAZULI" ||
                name == "NETHERITE_SCRAP" || name.startsWith("RAW_") -> ORES

                material.isBlock -> BLOCKS

                else -> OTHER
            }
        }
    }
}