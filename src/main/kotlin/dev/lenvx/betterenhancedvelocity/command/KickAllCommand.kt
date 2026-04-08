package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import dev.lenvx.betterenhancedvelocity.util.toComponent
import java.util.concurrent.CompletableFuture

class KickAllCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.KICKALL)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.KICKALL))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(Message.KICKALL_USAGE)
            return
        }

        val serverName = args[0].lowercase()
        val targetPlayers = if (serverName == "all") {
            server.allPlayers
        } else {
            server.getServer(serverName).map { it.playersConnected }.orElse(null)
        }

        if (targetPlayers == null) {
            sender.sendMessage(Message.KICKALL_NO_SERVER)
            return
        }

        val reason = Message.KICKALL_REASON.get().toComponent()
        targetPlayers
            .filterNot { it.hasPermission(Permissions.Actions.KICKALL_BYPASS) }
            .forEach { it.disconnect(reason) }

        sender.sendMessage(Message.KICKALL_USE, TextReplacement("server", serverName))
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val lastArg = invocation.arguments().lastOrNull()?.lowercase() ?: ""
        return (server.allServers.map { it.serverInfo.name } + "all")
            .filter { it.lowercase().startsWith(lastArg) }
            .sorted()
    }

    override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> =
        CompletableFuture.completedFuture(suggest(invocation))
}
