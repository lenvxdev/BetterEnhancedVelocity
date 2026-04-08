package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class MoveCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.MOVE)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.MOVE))
            return
        }

        if (args.size < 2) {
            sender.sendMessage(Message.MOVE_USAGE)
            return
        }

        val targetSelector = args[0].lowercase()
        val targetServerName = args[1]

        val destination = server.getServer(targetServerName).orElse(null)
        if (destination == null) {
            sender.sendMessage(Message.SERVER_NOT_FOUND, TextReplacement("server", targetServerName))
            return
        }

        val playersToMove: List<Player> = when {
            targetSelector == "all" -> {
                if (!sender.hasPermission(Permissions.Actions.MOVE_ALL)) {
                    sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Actions.MOVE_ALL))
                    return
                }
                server.allPlayers.toList()
            }

            targetSelector.startsWith("server:") -> {
                if (!sender.hasPermission(Permissions.Actions.MOVE_SERVER)) {
                    sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Actions.MOVE_SERVER))
                    return
                }
                val sourceServerName = targetSelector.substringAfter("server:")
                val sourceServer = server.getServer(sourceServerName).orElse(null)
                if (sourceServer == null) {
                    sender.sendMessage(Message.SERVER_NOT_FOUND, TextReplacement("server", sourceServerName))
                    return
                }
                sourceServer.playersConnected.toList()
            }

            else -> {
                if (!sender.hasPermission(Permissions.Actions.MOVE_PLAYER)) {
                    sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Actions.MOVE_PLAYER))
                    return
                }
                val player = server.getPlayer(targetSelector).orElse(null)
                if (player == null) {
                    sender.sendMessage(Message.PLAYER_NOT_FOUND, TextReplacement("player", targetSelector))
                    return
                }
                listOf(player)
            }
        }

        if (playersToMove.isEmpty()) {
            sender.sendMessage(Message.MOVE_NO_PLAYERS_FOUND)
            return
        }

        val destinationName = destination.serverInfo.name
        val alreadyOnServer = playersToMove.filter { player ->
            player.currentServer.map { it.serverInfo.name }.orElse("") == destinationName
        }
        val toMove = playersToMove - alreadyOnServer.toSet()

        if (alreadyOnServer.isNotEmpty()) {
            sender.sendMessage(
                Message.MOVE_ALREADY_ON_SERVER,
                TextReplacement("count", alreadyOnServer.size.toString()),
                TextReplacement("server", destinationName)
            )
        }

        if (toMove.isEmpty()) {
            return
        }

        val movedCount = AtomicInteger(0)
        val failedCount = AtomicInteger(0)
        val pending = AtomicInteger(toMove.size)

        for (player in toMove) {
            player.createConnectionRequest(destination).connect().thenAccept { result ->
                if (result.isSuccessful) movedCount.incrementAndGet() else failedCount.incrementAndGet()
                if (pending.decrementAndGet() == 0) {
                    sender.sendMessage(
                        Message.MOVE_SUCCESS,
                        TextReplacement("count", movedCount.get().toString()),
                        TextReplacement("destination", destinationName)
                    )
                    if (failedCount.get() > 0) {
                        sender.sendMessage(
                            Message.MOVE_FAILED,
                            TextReplacement("count", failedCount.get().toString())
                        )
                    }
                }
            }
        }
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val args = invocation.arguments()
        val lastArg = args.lastOrNull()?.lowercase() ?: ""
        return when (args.size) {
            1 -> {
                val suggestions = mutableListOf("all")
                server.allServers.mapTo(suggestions) { "server:${it.serverInfo.name}" }
                server.allPlayers.mapTo(suggestions) { it.username }
                suggestions.filter { it.lowercase().startsWith(lastArg) }.sorted()
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
