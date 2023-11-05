package me.clicks

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

/**
 * @author Clicks
*/
@Mod(
    modid = "livid",
    name = "Livid",
    version = "0.0.0",
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Livid {

    val mc: Minecraft by lazy {
        Minecraft.getMinecraft()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        println("Livid loaded!")
    }

}
