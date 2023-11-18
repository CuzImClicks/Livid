package me.clicks

import me.clicks.features.Pickobulus
import me.clicks.mixins.AccessorRenderManager
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
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

    val renderManager: AccessorRenderManager by lazy {
        mc.renderManager as AccessorRenderManager
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        println("Livid loaded!")

        arrayOf(
            Pickobulus
        ).forEach {
            MinecraftForge.EVENT_BUS.register(it)
        }
    }

}
