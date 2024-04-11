package me.clicks.utils

import com.google.gson.JsonArray
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraftforge.client.event.sound.SoundEvent
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.math.roundToInt

object Utils {

    var playingSound = false

    /**
     * Returns a String of an Array that does not contain "[", "]" and ","
     * @param array The Array that should be turned into a String
     * @return The Array in formatted String form
     */
    fun getArrayAsFormattedString(array: Array<String>): String {
        return array.contentToString()
            .replace(",", "")
            .replace("[", "")
            .replace("]", "")
    }

    /**
     * Checks if the inputStr contains any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun Array<String>.containsAny(inputStr: String): Boolean {
        return Arrays.stream(this).anyMatch { s: String ->
            inputStr.contains(
                s
            )
        }
    }

    infix fun Array<String>.containsIgnoreCase(inputStr: String): Boolean {
        return Arrays.stream(this).anyMatch { item: String ->
            inputStr.lowercase(Locale.getDefault()).contains(
                item
            )
        }
    }

    /**
     * Checks if the inputStr contains any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun List<String>.containsAny(inputStr: String): Boolean {
        return this.stream().anyMatch { s: String ->
            inputStr.contains(
                s
            )
        }
    }

    infix fun List<String>.containsIgnoreCase(inputStr: String): Boolean {
        return this.stream().anyMatch { item: String ->
            inputStr.lowercase(Locale.getDefault()).contains(
                item
            )
        }
    }

    /**
     * Checks if the inputStr equals any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun List<String>.equalsAny(inputStr: String): Boolean {
        return this.stream().anyMatch { anotherString: String -> inputStr == anotherString }
    }

    /**
     * Checks if the inputStr equals any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun Array<String>.equalsAny(inputStr: String): Boolean {
        return Arrays.stream(this).anyMatch { anotherString: String -> inputStr == anotherString }
    }

    /**
     * Checks if the inputStr equals any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun List<String>.equalsAnyIgnoreCase(inputStr: String): Boolean {
        return this.stream().anyMatch { anotherString: String -> inputStr.equals(anotherString, ignoreCase = true) }
    }

    /**
     * Checks if the inputStr equals any of the item Strings
     *
     * @param inputStr The String that is checked
     * @return boolean if the inputStr contains any of the item Strings
     */
    infix fun Array<String>.equalsAnyIgnoreCase(inputStr: String): Boolean {
        return Arrays.stream(this).anyMatch { anotherString: String -> inputStr.equals(anotherString, ignoreCase = true) }
    }

    /**
     * When you use this function, any sound played will bypass the player's
     * volume setting, so make sure to only use this for like warnings or stuff like that.
     * @param sound The sound that is played, use the SoundEvents enum for all default sounds
     * @param pitch The percentage at what pitch the sound should be played
     */
    fun playLoudSound(sound: SoundEvent, pitch: Double) {
        playingSound = true
        Minecraft.getMinecraft().thePlayer.playSound(sound.toString(), 1f, pitch.toFloat())
        playingSound = false
    }

    /**
     * Converts a JsonArray to a MutableList of Any
     */
    fun JsonArray.toMutableList(): MutableList<Any> {
        return this.map { it }.toMutableList()
    }

    /**
     * Converts a boolean to a on or off with colour
     *
     * @param bool The boolean that is converted
     * @return The boolean in a formatted String with colour
     */
    fun getColouredBoolean(bool: Boolean): String {
        return if (bool) EnumChatFormatting.GREEN.toString() + "On" else EnumChatFormatting.RED.toString() + "Off"
    }

    fun String.copyToClipboard() {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val str = StringSelection(this)
        clipboard.setContents(str, null)
    }

    // Taken from SkyblockAddons
    fun ItemStack.getItemLore(): List<String> {
        val nbtString = 8
        val nbtList = 9
        val nbtCompound = 10
        if (this.hasTagCompound() && this.tagCompound!!.hasKey("display", nbtCompound)) {
            val display = this.tagCompound!!.getCompoundTag("display")
            if (display.hasKey("Lore", nbtList)) {
                val lore = display.getTagList("Lore", nbtString)
                val loreAsList: MutableList<String> = ArrayList()
                for (lineNumber in 0 until lore.tagCount()) {
                    loreAsList.add(lore.getStringTagAt(lineNumber))
                }
                return Collections.unmodifiableList(loreAsList)
            }
        }
        return emptyList()
    }

    infix fun <T> Iterable<T>.remove(other: Iterable<T>): MutableList<T> {
        return this.filter { it !in other }.toMutableList()
    }

    fun getRayTraceResult(): MovingObjectPosition {
        return Minecraft.getMinecraft().objectMouseOver
    }

    fun Array<Any>.constructMultilineTooltip(): String {
        var tooltip = ""
        this.forEach {
            tooltip += "$it\n"
        }
        return tooltip.replace("\\n$".toRegex(), "")
    }

    fun Iterable<Any>.constructMultilineTooltip(): String {
        var tooltip = ""
        this.forEach {
            tooltip += "$it\n"
        }
        return tooltip.replace("\\n$".toRegex(), "")
    }

    fun getCurrentWorkingDirectory(): File {
        return File(System.getProperty("user.dir"))
    }
}

/**
 * When you use this function, any sound played will bypass the player's
 * volume setting, so make sure to only use this for like warnings or stuff like that.
 * @param sound The sound that is played
 * @param pitch The percentage at what pitch the sound should be played
 */
fun playSound(sound: String, volume: Float = 1f, pitch: Double) {
    Minecraft.getMinecraft().thePlayer.playSound(sound, volume, pitch.toFloat())
}

/**
 * Copies the given text to the clipboard
 */
fun String.copyToClipboard() {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(this), null)
}

fun String.stripControlCodes(): String{
    return StringUtils.stripControlCodes(this)
}

fun getFirstBlockPosAfterVectors(mc: Minecraft, pos1: Vec3, pos2: Vec3, strength: Int, distance: Int): BlockPos? {
    val x = pos2.xCoord - pos1.xCoord
    val y = pos2.yCoord - pos1.yCoord
    val z = pos2.zCoord - pos1.zCoord
    for (i in strength until distance * strength) { // Start at least 1 strength away
        val newX = pos1.xCoord + x / strength * i
        val newY = pos1.yCoord + y / strength * i
        val newZ = pos1.zCoord + z / strength * i
        val newBlock = BlockPos(newX, newY, newZ)
        if (mc.theWorld.getBlockState(newBlock).block !== Blocks.air) {
            return newBlock
        }
    }
    return null
}

fun String.validatePattern(): Boolean {
    return try {
        Pattern.compile(this)
        true
    } catch (e: PatternSyntaxException) {
        false
    }
}

fun BlockPos.toAxisAlignedBB(): AxisAlignedBB {
    return AxisAlignedBB(this.x.toDouble() - 0.001, this.y.toDouble() - 0.001, this.z.toDouble() - 0.001, this.x.toDouble() + 1.001, this.y.toDouble() + 1.001, this.z.toDouble() + 1.001)
}

fun getRainbowColor(speed: Float, offset: Float): Int {
    val hue = (System.currentTimeMillis() + offset) % (360 * speed)
    return Color.getHSBColor(hue / (360 * speed), 1f, 1f).rgb
}

fun getRainbowColor2(): Int {
    val hue = (System.currentTimeMillis() % 5000L) / 5000.0f  // Vary the hue over time

    // Convert HSV to RGB
    val rgb = Color.HSBtoRGB(hue, 0.5f, 0.5f)

    val red = rgb shr 16 and 0xFF
    val green = rgb shr 8 and 0xFF
    val blue = rgb and 0xFF

    return (red shl 16) or (green shl 8) or blue
}

fun AxisAlignedBB.expandBlock(): AxisAlignedBB =
    expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)


fun BlockPos.compareTo(vec3: Vec3): Boolean {
    return this.x == vec3.xCoord.roundToInt() && this.y == vec3.yCoord.roundToInt() && this.z == vec3.zCoord.roundToInt()
}
