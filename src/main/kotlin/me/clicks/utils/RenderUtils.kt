package me.clicks.utils

/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2022 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import me.clicks.Livid.mc
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


object RenderUtil {
    private val beaconBeam = ResourceLocation("textures/entity/beacon_beam.png")

    private fun renderBeaconBeam(
        x: Double, y: Double, z: Double, rgb: Int, alphaMult: Float,
        partialTicks: Float, disableDepth: Boolean
    ) {
        val height = 300
        val bottomOffset = 0
        val topOffset = bottomOffset + height
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        if (disableDepth) {
            GlStateManager.disableDepth()
        }
        Minecraft.getMinecraft().textureManager.bindTexture(beaconBeam)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT.toFloat())
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT.toFloat())
        GlStateManager.disableLighting()
        GlStateManager.enableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        val time = Minecraft.getMinecraft().theWorld.totalWorldTime + partialTicks.toDouble()
        val d1 = MathHelper.func_181162_h(-time * 0.2 - MathHelper.floor_double(-time * 0.1).toDouble())
        val r = (rgb shr 16 and 0xFF) / 255f
        val g = (rgb shr 8 and 0xFF) / 255f
        val b = (rgb and 0xFF) / 255f
        val d2 = time * 0.025 * -1.5
        val d4 = 0.5 + cos(d2 + 2.356194490192345) * 0.2
        val d5 = 0.5 + sin(d2 + 2.356194490192345) * 0.2
        val d6 = 0.5 + cos(d2 + Math.PI / 4.0) * 0.2
        val d7 = 0.5 + sin(d2 + Math.PI / 4.0) * 0.2
        val d8 = 0.5 + cos(d2 + 3.9269908169872414) * 0.2
        val d9 = 0.5 + sin(d2 + 3.9269908169872414) * 0.2
        val d10 = 0.5 + cos(d2 + 5.497787143782138) * 0.2
        val d11 = 0.5 + sin(d2 + 5.497787143782138) * 0.2
        val d14 = -1.0 + d1
        val d15 = height.toDouble() * 2.5 + d14
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldRenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        worldRenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldRenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0, d15).color(r, g, b, 1.0f * alphaMult).endVertex()
        tessellator.draw()
        GlStateManager.disableCull()
        val d12 = -1.0 + d1
        val d13 = height + d12
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldRenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alphaMult).endVertex()
        tessellator.draw()
        GlStateManager.disableLighting()
        GlStateManager.enableTexture2D()
        if (disableDepth) {
            GlStateManager.enableDepth()
        }
    }


    /**
     * Taken from NotEnoughUpdates under Creative Commons Attribution-NonCommercial 3.0
     * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
     * @author Moulberry
     * @author Mojang
     */
    fun drawFilledBoundingBox(aabb: AxisAlignedBB, c: Color, alphaMultiplier: Float = 1f) {
        GlStateManager.enableBlend()
        GlStateManager.disableLighting()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        GlStateManager.color(c.red / 255f, c.green / 255f, c.blue / 255f, c.alpha / 255f * alphaMultiplier)

        //vertical
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        tessellator.draw()
        GlStateManager.color(
            c.red / 255f * 0.8f,
            c.green / 255f * 0.8f,
            c.blue / 255f * 0.8f,
            c.alpha / 255f * alphaMultiplier
        )

        //x
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.color(
            c.red / 255f * 0.9f,
            c.green / 255f * 0.9f,
            c.blue / 255f * 0.9f,
            c.alpha / 255f * alphaMultiplier
        )
        //z
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    /**
     * @author Mojang
     */
    private fun drawNametag(str: String) {
        val fontRenderer = mc.fontRendererObj
        val f1 = 0.02666667f
        GlStateManager.pushMatrix()
        GL11.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-mc.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(
            mc.renderManager.playerViewX,
            1.0f,
            0.0f,
            0.0f
        )
        GlStateManager.scale(-f1, -f1, f1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldRender = tessellator.worldRenderer
        val i = 0
        val j = fontRenderer.getStringWidth(str) / 2
        GlStateManager.disableTexture2D()
        GlStateManager.disableDepth()
        worldRender.begin(7, DefaultVertexFormats.POSITION_COLOR)
        worldRender.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldRender.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldRender.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldRender.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        tessellator.draw()
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        fontRenderer.drawString(str, -j, i, 553648127)
        GlStateManager.depthMask(true)
        fontRenderer.drawString(str, -j, i, -1)
        GlStateManager.enableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    fun getViewerPos(partialTicks: Float): Triple<Double, Double, Double> {
        val viewer = mc.renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        return Triple(viewerX, viewerY, viewerZ)
    }

    infix fun Slot.highlight(color: Color) {
        Gui.drawRect(
            this.xDisplayPosition,
            this.yDisplayPosition,
            this.xDisplayPosition + 16,
            this.yDisplayPosition + 16,
            color.rgb
        )
    }

    fun drawOnSlot(size: Int, xSlotPos: Int, ySlotPos: Int, colour: Int) {
        val sr = ScaledResolution(Minecraft.getMinecraft())
        val guiLeft = (sr.scaledWidth - 176) / 2
        val guiTop = (sr.scaledHeight - 222) / 2
        val x = guiLeft + xSlotPos
        var y = guiTop + ySlotPos
        // Move down when chest isn't 6 rows
        if (size != 90) y += (6 - (size - 36) / 9) * 9
        GL11.glTranslated(0.0, 0.0, 1.0)
        Gui.drawRect(x, y, x + 16, y + 16, colour)
        GL11.glTranslated(0.0, 0.0, -1.0)
    }

    fun interpolate(currentValue: Double, lastValue: Double, multiplier: Float): Double {
        return lastValue + (currentValue - lastValue) * multiplier
    }

    fun renderBoundingBox(x: Double, y: Double, z: Double, rgb: Int, alphaMult: Float, partialTicks: Float) {
        val bb = AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)
        GlStateManager.disableDepth()
        GlStateManager.disableCull()
        GlStateManager.disableTexture2D()
        drawFilledBoundingBox(bb, Color(rgb), partialTicks)
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
    }

    fun renderBeaconBeam(block: BlockPos, rgb: Int, alphaMult: Float, partialTicks: Float) {
        val viewer = Minecraft.getMinecraft().renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks

        val x = block.x - viewerX
        val y = block.y - viewerY
        val z = block.z - viewerZ
        val distSq = x * x + y * y + z * z
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GlStateManager.disableDepth()
        renderBeaconBeam(x, y, z, rgb, alphaMult , partialTicks, distSq > 10 * 10)
        GlStateManager.enableDepth()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    fun renderBeaconBeamOrBoundingBox(block: BlockPos, rgb: Int, alphaMult: Float, partialTicks: Float) {

        val viewer = Minecraft.getMinecraft().renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks

        val x = block.x - viewerX
        val y = block.y - viewerY
        val z = block.z - viewerZ
        val distSq = x * x + y * y + z * z
        if (distSq > 10 * 10) {
            renderBeaconBeam(x, y, z, rgb, alphaMult, partialTicks, true)
        }

        renderBoundingBox(x, y, z, rgb, alphaMult, partialTicks)

    }


    fun renderWayPoint(str: String, loc: Vec3i, partialTicks: Float) {
        renderWayPoint(str, Vector3f(loc.x.toFloat(), loc.y.toFloat(), loc.z.toFloat()), partialTicks)
    }
    fun renderWayPoint(str: String, loc: Vector3f, partialTicks: Float) {
        renderWayPoint(mutableListOf(str), loc, partialTicks)
    }

    fun renderWayPoint(lines: MutableList<String>, loc: Vector3f, partialTicks: Float) {
        var lines = lines
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.pushMatrix()
        val viewer = Minecraft.getMinecraft().renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        var x: Double = loc.x - viewerX + 0.5f
        var y: Double = loc.y - viewerY - viewer.eyeHeight
        var z: Double = loc.z - viewerZ + 0.5f
        val distSq = x * x + y * y + z * z
        val dist = sqrt(distSq)
        if (distSq > 144) {
            x *= 12 / dist
            y *= 12 / dist
            z *= 12 / dist
        }
        GlStateManager.translate(x, y, z)
        GlStateManager.translate(0f, viewer.eyeHeight, 0f)
        lines = ArrayList(lines)
        lines.add(EnumChatFormatting.YELLOW.toString() + dist.roundToInt() + "m")
        renderNametag(lines)
        GlStateManager.popMatrix()
        GlStateManager.disableLighting()
    }

    fun renderNametag(lines: List<String?>) {
        val fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRendererObj
        val f = 1.6f
        val f1 = 0.016666668f * f
        GlStateManager.pushMatrix()
        GL11.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-Minecraft.getMinecraft().renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(Minecraft.getMinecraft().renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(-f1, -f1, f1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.disableDepth()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        val i = 0
        for (str in lines) {
            val j: Int = fontRenderer.getStringWidth(str) / 2
            GlStateManager.disableTexture2D()
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            worldRenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, i, 553648127)
            GlStateManager.depthMask(true)
            fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, i, -1)
            GlStateManager.translate(0f, 10f, 0f)
        }
        GlStateManager.enableDepth()
        GlStateManager.enableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

     fun drawFilled3DBox(
         aabb: AxisAlignedBB,
         colourInt: Int,
         translucent: Boolean,
         depth: Boolean,
         partialTicks: Float
     ) {
         val render: Entity = Minecraft.getMinecraft().renderViewEntity
         val worldRenderer = Tessellator.getInstance().worldRenderer
         val colour = Color(colourInt)
         val realX: Double = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks
         val realY: Double = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks
         val realZ: Double = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks
         GlStateManager.pushMatrix()
         GlStateManager.pushAttrib()
         GlStateManager.translate(-realX, -realY, -realZ)
         GlStateManager.disableTexture2D()
         GlStateManager.enableAlpha()
         GlStateManager.enableBlend()
         GlStateManager.disableCull()
         GlStateManager.tryBlendFuncSeparate(770, if (translucent) 1 else 771, 1, 0)
         if (!depth) {
             GL11.glDisable(GL11.GL_DEPTH_TEST)
             GlStateManager.depthMask(false)
         }
         GlStateManager.color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f)
         worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
         // Bottom
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
         // Top
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
         // West
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
         // East
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
         // North
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
         // South
         worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
         worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
         Tessellator.getInstance().draw()
         GlStateManager.translate(realX, realY, realZ)
         if (!depth) {
             GL11.glEnable(GL11.GL_DEPTH_TEST)
             GlStateManager.depthMask(true)
         }
         GlStateManager.enableCull()
         GlStateManager.disableAlpha()
         GlStateManager.disableBlend()
         GlStateManager.enableTexture2D()
         GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
         GlStateManager.popAttrib()
         GlStateManager.popMatrix()
     }

    fun BlockPos.draw3DString(text: String, colour: Int, partialTicks: Float) {
        val mc = Minecraft.getMinecraft()
        val player: EntityPlayer = mc.thePlayer
        val x = this.x - player.lastTickPosX + (this.x - player.posX - (this.x - player.lastTickPosX)) * partialTicks
        val y = this.y - player.lastTickPosY + (this.y - player.posY - (this.y - player.lastTickPosY)) * partialTicks
        val z = this.z - player.lastTickPosZ + (this.z - player.posZ - (this.z - player.lastTickPosZ)) * partialTicks
        val renderManager = mc.renderManager
        val f = 1.6f
        val f1 = 0.016666668f * f
        val width = mc.fontRendererObj.getStringWidth(text) / 2
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        GL11.glNormal3f(0f, 1f, 0f)
        GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f)
        GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f)
        GlStateManager.scale(-f1, -f1, -f1)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        mc.fontRendererObj.drawString(text, -width, 0, colour)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun draw3DString(x: Double, y: Double, z: Double, text: String?, colour: Int, partialTicks: Float) {
        val mc = Minecraft.getMinecraft()
        val player: EntityPlayer = mc.thePlayer
        val realX = x - player.lastTickPosX + (x - player.posX - (x - player.lastTickPosX)) * partialTicks
        val realY = y - player.lastTickPosY + (y - player.posY - (y - player.lastTickPosY)) * partialTicks
        val realZ = z - player.lastTickPosZ + (z - player.posZ - (z - player.lastTickPosZ)) * partialTicks
        val renderManager = mc.renderManager
        val f = 1.6f
        val f1 = 0.016666668f * f
        val width = mc.fontRendererObj.getStringWidth(text) / 2
        GlStateManager.pushMatrix()
        GlStateManager.translate(realX, realY, realZ)
        GL11.glNormal3f(0f, 1f, 0f)
        GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f)
        GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f)
        GlStateManager.scale(-f1, -f1, -f1)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        mc.fontRendererObj.drawString(text, -width, 0, colour)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun AxisAlignedBB(x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int): AxisAlignedBB {
        return AxisAlignedBB(x.toDouble(), y.toDouble(), z.toDouble(), x2.toDouble(), y2.toDouble(), z2.toDouble())
    }

    fun draw3DBox(aabb: AxisAlignedBB?, colourInt: Int, partialTicks: Float, depth: Boolean = true) {
        val render = Minecraft.getMinecraft().renderViewEntity
        val colour = Color(colourInt, true)
        val realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks
        val realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks
        val realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks
        GlStateManager.pushMatrix()
        GlStateManager.translate(-realX, -realY, -realZ)
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        if (!depth) {
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GlStateManager.depthMask(false)
        }
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GL11.glLineWidth(2f)
        RenderGlobal.drawOutlinedBoundingBox(aabb, colour.red, colour.green, colour.blue, colour.alpha)
        GlStateManager.translate(realX, realY, realZ)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        if (!depth) {
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GlStateManager.depthMask(true)
        }
        GlStateManager.enableTexture2D()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }
}
