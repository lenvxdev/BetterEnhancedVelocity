package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.api.VanishManager
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import java.util.concurrent.CompletableFuture

class SendCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.SEND)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.SEND))
            return
        }

        if (args.size < 2) {
            sender.sendMessage(Message.SEND_USAGE)
            return
        }

        val target = server.getPlayer(args[0]).orElse(null)
        if (target == null || (VanishManager.isVanished(target.uniqueId) && !sender.hasPermission(Permissions.Actions.SEE_VANISHED))) {
            sender.sendMessage(Message.PLAYER_NOT_FOUND)
            return
        }

        val destination = server.getServer(args[1]).orElse(null)
        if (destination == null) {
            sender.sendMessage(Message.SERVER_NOT_FOUND)
            return
        }

        target.createConnectionRequest(destination).fireAndForget()
        sender.sendMessage(
            Message.SEND_USE,
            TextReplacement("player", target.username),
            TextReplacement("server", destination.serverInfo.name)
        )
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val args = invocation.arguments()
        val lastArg = args.lastOrNull()?.lowercase() ?: ""
        return when (args.size) {
            1 -> {
                val players = if (invocation.source().hasPermission(Permissions.Actions.SEE_VANISHED)) {
                    server.allPlayers
                } else {
                    VanishManager.getNonVanishedPlayers(server)
                }
                players.map { it.username }.filter { it.lowercase().startsWith(lastArg) }.sorted()
            }
            2 -> server.allServers.map { it.serverInfo.name }
                .filter { it.lowercase().startsWith(lastArg) }
                .sorted()
            else -> emptyList()
        }
    }

    override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> =
        CompletableFuture.completedFuture(suggest(invocation))
}
