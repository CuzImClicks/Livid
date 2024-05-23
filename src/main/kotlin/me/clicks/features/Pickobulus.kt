package me.clicks.features

import gg.essential.elementa.utils.withAlpha
import me.clicks.Livid
import me.clicks.Livid.mc
import me.clicks.utils.*
import net.minecraft.block.BlockCarpet
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.round


object Pickobulus {

    var highestDensityBlock: Vec3? = null
    var highestDensityBlockAxisAlignedBB: AxisAlignedBB? = null
    
    val pickobulusRegex = Regex("^Your Pickobulus destroyed (?<amount>\\d+) blocks!$")
    var isPickobulusReady = false
    var enabled = false
    var lastUsed: Long? = null

    // the thread running in the background detecting the blocks
    val thread = Thread( {
        while (enabled) {
            Thread.sleep(200)
            if (lastUsed != null && System.currentTimeMillis() - lastUsed!! > 120000L) isPickobulusReady = true
            mc.thePlayer ?: return@Thread
            mc.theWorld ?: return@Thread
            val block = mc.thePlayer.rayTrace(100.0, 1.0f) // might change to pickobulus range
            block ?: continue
            if (block.blockPos == null) {
                highestDensityBlock = null
                highestDensityBlockAxisAlignedBB = null
                continue
            }
            val state = mc.theWorld.getBlockState(block.blockPos)
            if (state.block != Blocks.prismarine && state.block != Blocks.wool) { // when we look at stone
                highestDensityBlock = null
                highestDensityBlockAxisAlignedBB = null
                continue
            }
            if (state.block == Blocks.wool) {
                if (state.properties[BlockCarpet.COLOR] != EnumDyeColor.LIGHT_BLUE) {
                    highestDensityBlock = null
                    highestDensityBlockAxisAlignedBB = null
                    continue
                }
            }
            val q = arrayListOf(block.blockPos) // queue
            val bs: ArrayList<BlockPos> = arrayListOf() // blocks
            if (!bs.contains(block.blockPos)) {
                while (q.size > 0) {
                    val b = q.removeAt(0)
                    if (bs.contains(b)) continue
                    val blockState = mc.theWorld.getBlockState(b)
                    if (blockState.block != Blocks.prismarine && blockState.block != Blocks.wool) continue
                    if (blockState.block == Blocks.wool) {
                        if (blockState.properties[BlockCarpet.COLOR] != EnumDyeColor.LIGHT_BLUE) continue
                    }
                    bs.add(b)
                    val blocksInBox = BlockPos.getAllInBox(b.add(-1, -1, -1), b.add(1, 1, 1))
                    blocksInBox.forEach { blockPos ->
                        if (!q.contains(blockPos) && !bs.contains(blockPos)) {
                            q.add(blockPos)
                        }
                    }
                }
                findBlocksWithHighestDensity(bs, 6).let {
                    // doesnt work when we have two maxima apart from eachother
                    // since crystal hollows mithril veins have only one nucleus
                    // if we have two maxima right next to eachother it takes the middle of the two
                    val xValues = it.map { blockPos -> blockPos.x.toDouble() }
                    val yValues = it.map { blockPos -> blockPos.y.toDouble() }
                    val zValues = it.map { blockPos -> blockPos.z.toDouble() }
                    val x = xValues.sum() / xValues.size.toDouble()
                    val y = yValues.sum() / yValues.size.toDouble()
                    val z = zValues.sum() / zValues.size.toDouble()
                    highestDensityBlock = Vec3(x, y, z)
                    highestDensityBlockAxisAlignedBB = AxisAlignedBB(
                        highestDensityBlock!!.xCoord,
                        highestDensityBlock!!.yCoord,
                        highestDensityBlock!!.zCoord,
                        highestDensityBlock!!.xCoord + 1,
                        highestDensityBlock!!.yCoord + 1,
                        highestDensityBlock!!.zCoord + 1,
                    )
                }
            }
        }
    }, "Pickobulus")

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        if (!enabled || !isPickobulusReady) return
        highestDensityBlock ?: return
        highestDensityBlockAxisAlignedBB ?: return
        val color = if (mc.thePlayer.rayTrace(5.0, event.partialTicks).blockPos.compareTo(highestDensityBlock!!)) {
            Color.GREEN.withAlpha(100)
        } else {
            Color.RED.withAlpha(100)

        }
        RenderUtil.drawFilled3DBox(
            highestDensityBlockAxisAlignedBB!!.contract(0.4, 0.4, 0.4),
            color.rgb,
            translucent = true,
            depth = false,
            partialTicks = event.partialTicks
        )
        highestDensityBlock?.let { drawTracer(it, color) }
    }

    fun findBlocksWithHighestDensity(blocks: Iterable<BlockPos?>, n: Int): Array<BlockPos> {
        if (n <= 0) {
            return arrayOf()
        }

        var maxDensity = 0.0
        val blocksWithMaxDensity: ArrayList<BlockPos> = arrayListOf()

        for (block in blocks) {
            if (block == null) continue
            val density = calculateBlocks(blocks, block, n)
            if (density > maxDensity) {
                maxDensity = density
                blocksWithMaxDensity.clear()
                blocksWithMaxDensity.add(block)
            } else if (density == maxDensity) {
                blocksWithMaxDensity.add(block)
            }
        }
        return blocksWithMaxDensity.toTypedArray()
    }

    fun calculateBlocks(blocks: Iterable<BlockPos?>, center: BlockPos, n: Int): Double {
        return blocks.filter {
            if (it != null) {
                isWithinCube(center, it, n)
            }
            false
        }.size.toDouble()
    }

    fun isWithinCube(center: BlockPos, target: BlockPos, n: Int): Boolean {
        val halfSize = n / 2.0

        val withinX = target.x >= center.x - halfSize && target.x <= center.x + halfSize
        val withinY = target.y >= center.y - halfSize && target.y <= center.y + halfSize
        val withinZ = target.z >= center.z - halfSize && target.z <= center.z + halfSize

        return withinX && withinY && withinZ
    }
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        highestDensityBlock = null
        isPickobulusReady = false
        val area = HypixelUtils.getArea()
        enabled = area == "Dwarven Mines" || area == "Crystal Hollows" || area == "Deep Caverns"
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        highestDensityBlock = null
        isPickobulusReady = false
        enabled = false
    }

    @SubscribeEvent()
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText.stripControlCodes() == "Pickobulus is now available!") {
            val area = HypixelUtils.getArea()
            enabled = area == "Dwarven Mines" || area == "Crystal Hollows" || area == "Deep Caverns"
            isPickobulusReady = true
            if (enabled && !thread.isAlive) thread.start()
        }
        if (event.message.unformattedText.stripControlCodes() == "You used your Pickobulus Pickaxe Ability!") {
            highestDensityBlock = null
            highestDensityBlockAxisAlignedBB = null
            isPickobulusReady = false
            lastUsed = System.currentTimeMillis()
        }
        val result = pickobulusRegex.matchEntire(event.message.unformattedText.stripControlCodes())
        if (result?.groups?.get("amount")?.value != null) {
            val amount = result.groups["amount"]!!.value.toFloat()
            if (amount > 0) {
                mc.thePlayer.addChatMessage("" +
                        "${EnumChatFormatting.GREEN}Pickobulus" +
                        "${EnumChatFormatting.GRAY} had " +
                        "${if (amount == 214.0f) EnumChatFormatting.GREEN else EnumChatFormatting.RED}" +
                        "${round((amount / 214) * 100)}% " +
                        "${EnumChatFormatting.GRAY}" +
                        "efficiency" +
                        "${EnumChatFormatting.RESET}")
                if (amount < 214) {
                    mc.thePlayer.addChatMessage("${EnumChatFormatting.GOLD}${214 - amount} ${EnumChatFormatting.GRAY}blocks were not destroyed")
                }
            }
        }
    }

    fun drawTracer(pos: Vec3, color: Color) {
        val renderManager = Livid.renderManager
        val x = pos.xCoord - renderManager.renderPosX
        val y = pos.yCoord - renderManager.renderPosY
        val z = pos.zCoord - renderManager.renderPosZ
        val eyeVector = Vec3(0.0, 0.0, 1.0).rotatePitch(
            -Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationPitch.toDouble()).toFloat()
        ).rotateYaw(-Math.toRadians(Minecraft.getMinecraft().thePlayer.rotationYaw.toDouble()).toFloat())
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(3042)
        GL11.glLineWidth(2.0f)
        GL11.glDisable(3553)
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
        GL11.glBegin(1)
        GL11.glVertex3d(
            eyeVector.xCoord,
            Minecraft.getMinecraft().thePlayer.getEyeHeight() + eyeVector.yCoord,
            eyeVector.zCoord
        )
        GL11.glVertex3d(x + 0.5, y + 0.5, z + 0.5)
        GL11.glVertex3d(x, y, z)
        GL11.glVertex3d(x, y, z)
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glEnable(2929)
        GL11.glDepthMask(true)
        GL11.glDisable(3042)
        GlStateManager.resetColor()
    }
}
