package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.api.VanishManager
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.config.Settings
import dev.lenvx.betterenhancedvelocity.util.PlayerNotFoundException
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.getPlayerOrThrow
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import java.util.concurrent.CompletableFuture

class FindCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.FIND)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.FIND))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(Message.FIND_USAGE)
            return
        }

        val target = try {
            server.getPlayerOrThrow(args[0])
        } catch (e: PlayerNotFoundException) {
            sender.sendMessage(Message.FIND_NO_TARGET)
            return
        }

        val vanished = VanishManager.isVanished(target.uniqueId)

        if (vanished && !sender.hasPermission(Permissions.Actions.FIND_VANISHED)) {
            sender.sendMessage(Message.FIND_NO_TARGET)
            return
        }

        val serverName = target.currentServer.map { it.serverInfo.name }.orElse("Unknown")

        sender.sendMessage(
            Message.FIND_USE,
            TextReplacement("player", target.username),
            TextReplacement("server", serverName),
            TextReplacement(
                "vanished",
                if (vanished && sender.hasPermission(Permissions.Actions.FIND_VANISHED))
                    Settings.formatMessage(Message.FIND_VANISHED)
                else ""
            )
        )
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val lastArg = invocation.arguments().lastOrNull()?.lowercase() ?: ""
        return VanishManager.getNonVanishedPlayers(server)
            .map { it.username }
            .filter { it.lowercase().startsWith(lastArg) }
            .sorted()
    }

    override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> =
        CompletableFuture.completedFuture(suggest(invocation))
}
