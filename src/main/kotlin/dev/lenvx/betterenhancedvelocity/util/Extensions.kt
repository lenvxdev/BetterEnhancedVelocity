package dev.lenvx.betterenhancedvelocity.util

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.config.Message
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun CommandSource.sendMessage(message: Message, vararg replacements: TextReplacement) {
    sendMessage(message.get(*replacements))
}

fun CommandSource.sendMessage(message: String) {
    sendMessage(message.toComponent())
}

fun String.toComponent(vararg tags: TagResolver): Component =
    MiniMessage.miniMessage().deserialize(this, *tags)

fun String.toLegacyComponent(): Component =
    LegacyComponentSerializer.legacy('&').deserialize(this)

fun ProxyServer.getPlayerOrNull(name: String) = getPlayer(name).orElse(null)

@Throws(PlayerNotFoundException::class)
fun ProxyServer.getPlayerOrThrow(name: String) =
    getPlayer(name).orElse(null) ?: throw PlayerNotFoundException(name)
