package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.api.VanishManager
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import java.util.concurrent.CompletableFuture

class PingCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.PING)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.PING))
            return
        }

        if (args.isNotEmpty()) {
            val target = server.getPlayer(args[0]).orElse(null)
            if (target == null || (VanishManager.isVanished(target.uniqueId) && !sender.hasPermission(Permissions.Actions.SEE_VANISHED))) {
                sender.sendMessage(Message.PING_NO_TARGET)
                return
            }
            sender.sendMessage(
                Message.PING_USE_TARGET,
                TextReplacement("player", target.username),
                TextReplacement("ping", target.ping.toString())
            )
            return
        }

        if (sender !is Player) {
            sender.sendMessage(Message.ONLY_PLAYERS)
            return
        }

        sender.sendMessage(Message.PING_USE, TextReplacement("ping", sender.ping.toString()))
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val lastArg = invocation.arguments().lastOrNull()?.lowercase() ?: ""
        val players = if (invocation.source().hasPermission(Permissions.Actions.SEE_VANISHED)) {
            server.allPlayers
        } else {
            VanishManager.getNonVanishedPlayers(server)
        }
        return players.map { it.username }
            .filter { it.lowercase().startsWith(lastArg) }
            .sorted()
    }

    override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> =
        CompletableFuture.completedFuture(suggest(invocation))
}
