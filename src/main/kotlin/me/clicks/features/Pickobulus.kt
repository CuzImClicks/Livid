package me.clicks.features

import gg.essential.elementa.utils.withAlpha
import me.clicks.Livid
import me.clicks.Livid.mc
import me.clicks.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.round


object Pickobulus {

    val blocks = arrayListOf<BlockPos>()
    val queue = arrayListOf<BlockPos>()
    var highestDensityBlock: Vec3? = null
    var highestDensityBlockAxisAlignedBB: AxisAlignedBB? = null
    var isRunning = false
    val pickobulusRegex = Regex("^Your Pickobulus destroyed (?<amount>\\d+) blocks!$")
    var isPickobulusReady = false
    var tick = 0

    @SubscribeEvent
    fun onClientTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        tick++
        if (isPickobulusReady &&  tick % 10 == 0) {
            val block = mc.thePlayer.rayTrace(100.0, 1.0f)
            block ?: return
            if (block.blockPos == null) return
            val state = mc.theWorld.getBlockState(block.blockPos)
            if (state.block != Blocks.prismarine && state.block != Blocks.wool) return
            blocks.clear()
            queue.clear()
            queue.add(block.blockPos)
            if (!blocks.contains(block.blockPos)) {
                isRunning = true
                while (true) {
                    if (queue.size == 0 || blocks.size >= 1000) break
                    val b = queue.removeAt(0)
                    if (blocks.contains(b)) continue
                    val blockState = mc.theWorld.getBlockState(b)
                    if (blockState.block != Blocks.prismarine && blockState.block != Blocks.wool) continue
                    blocks.add(b)
                    queue.add(b.up())
                    queue.add(b.down())
                    queue.add(b.north())
                    queue.add(b.south())
                    queue.add(b.east())
                    queue.add(b.west())
                }
                findBlocksWithHighestDensity(blocks, 6).let { 
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
                isRunning = false
            }
        }
    }

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {

        for (i in 0 until blocks.size) {
            val hue = (i.toDouble() / (blocks.size - 1)).toFloat()  // Vary the hue across the blocks

            // Convert HSB to RGB
            val rgb = Color.HSBtoRGB(hue, .5f, .2f)

            val red = rgb shr 16 and 0xFF
            val green = rgb shr 8 and 0xFF
            val blue = rgb and 0xFF

            val color = (red shl 16) or (green shl 8) or blue

            val b = blocks[i]
            if (b == highestDensityBlock) continue
            RenderUtil.drawFilled3DBox(b.toAxisAlignedBB().expandBlock(), color, true, true, event.partialTicks)
        }
        highestDensityBlock ?: return
        RenderUtil.drawFilled3DBox(
        highestDensityBlockAxisAlignedBB!!.contract(0.4, 0.4, 0.4), 0xffffff, true, false, event.partialTicks)
        drawTracer(highestDensityBlock!!, Color(0xB000B5).withAlpha(1f))
    }

    fun findBlocksWithHighestDensity(blocks: ArrayList<BlockPos>, n: Int): Array<BlockPos> {
        if (blocks.isEmpty() || n <= 0) {
            return arrayOf()
        }

        var maxDensity = 0.0
        val blocksWithMaxDensity: ArrayList<BlockPos> = arrayListOf()

        for (block in blocks) {
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

    fun calculateBlocks(blocks: ArrayList<BlockPos>, center: BlockPos, n: Int): Double {
        return blocks.filter { isWithinCube(center, it, n) }.size.toDouble()
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
        blocks.clear()
        queue.clear()
        highestDensityBlock = null
        isPickobulusReady = false
    }

    @SubscribeEvent()
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText.stripControlCodes() == "Pickobulus is now available!") {
            isPickobulusReady = true
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
                blocks.clear()
                queue.clear()
                highestDensityBlock = null
                isPickobulusReady = false
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