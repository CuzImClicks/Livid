package me.clicks.utils

import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.util.ChatComponentText

/**
 * @author Clicks
 */

fun String.toChatComponentText(): ChatComponentText {
    return ChatComponentText(this)
}

fun AbstractClientPlayer.addChatMessage(message: String) {
    this.addChatMessage(ChatComponentText(message))
}
