package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import dev.lenvx.betterenhancedvelocity.util.toComponent

class BroadcastCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.BROADCAST)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.BROADCAST))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(Message.BROADCAST_USAGE)
            return
        }

        val raw = Message.BROADCAST_USE.get(TextReplacement("message", args.joinToString(" ")))
        val component = raw.toComponent()
        server.allPlayers.forEach { it.sendMessage(component) }
        sender.sendMessage(component)
    }
}
