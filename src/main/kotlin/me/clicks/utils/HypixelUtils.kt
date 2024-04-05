package me.clicks.utils

import net.minecraft.client.Minecraft

/**
 * @author Clicks
 */
object HypixelUtils {

    private val areaRegex: Regex = Regex("(Area|Dungeon): \\w+")

    fun getArea(): String {
        Minecraft.getMinecraft()?.netHandler?.playerInfoMap?.forEach {
            if (it.displayName?.unformattedText?.contains(areaRegex) == true) {
                return it.displayName.unformattedText.replace("Area: ", "").replace("Dungeon: ", "")
            }
        }
        return "Unknown"
    }

}
