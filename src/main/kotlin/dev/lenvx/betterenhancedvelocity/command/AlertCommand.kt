package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage

class AlertCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.ALERT)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.ALERT))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(Message.ALERT_USAGE)
            return
        }

        val message = args.joinToString(" ")
        server.allPlayers.forEach { it.sendMessage(Message.ALERT_USE, TextReplacement("message", message)) }
    }
}
