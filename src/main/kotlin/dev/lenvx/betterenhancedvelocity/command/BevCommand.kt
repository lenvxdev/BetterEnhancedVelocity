package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import dev.lenvx.betterenhancedvelocity.BetterEnhancedVelocity
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.config.Settings
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.sendMessage
import java.util.concurrent.CompletableFuture

class BevCommand(private val plugin: BetterEnhancedVelocity) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (!sender.hasPermission(Permissions.Commands.BEV)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.BEV))
            return
        }

        when (args.firstOrNull()?.lowercase()) {
            "reload" -> {
                plugin.reload()
                sender.sendMessage(Message.BEV_RELOAD_SUCCESS)
            }
            "help" -> sendHelp(sender)
            else -> sender.sendMessage(Message.BEV_USAGE)
        }
    }

    private fun sendHelp(sender: CommandSource) {
        sender.sendMessage(Message.HELP_HEADER)
        sender.sendMessage(Message.HELP_PLUGIN_HEADER, TextReplacement("plugin", "BetterEnhancedVelocity"))

        val entries = buildList {
            if (Settings.glistEnabled && sender.hasPermission(Permissions.Commands.GLIST))
                add(Settings.globalListCommand to Message.HELP_DESC_GLIST.get())
            if (Settings.findEnabled && sender.hasPermission(Permissions.Commands.FIND))
                add(Settings.findCommand to Message.HELP_DESC_FIND.get())
            if (Settings.sendEnabled && sender.hasPermission(Permissions.Commands.SEND))
                add(Settings.sendCommand to Message.HELP_DESC_SEND.get())
            if (Settings.alertEnabled && sender.hasPermission(Permissions.Commands.ALERT))
                add(Settings.alertCommand to Message.HELP_DESC_ALERT.get())
            if (Settings.broadcastEnabled && sender.hasPermission(Permissions.Commands.BROADCAST))
                add(Settings.broadcastCommand to Message.HELP_DESC_BROADCAST.get())
            if (Settings.pingEnabled && sender.hasPermission(Permissions.Commands.PING))
                add(Settings.pingCommand to Message.HELP_DESC_PING.get())
            if (Settings.kickAllEnabled && sender.hasPermission(Permissions.Commands.KICKALL))
                add(Settings.kickAllCommand to Message.HELP_DESC_KICKALL.get())
            if (Settings.moveEnabled && sender.hasPermission(Permissions.Commands.MOVE))
                add(Settings.moveCommand to Message.HELP_DESC_MOVE.get())
            add("bev" to Message.HELP_DESC_BEV.get())
        }

        for ((command, description) in entries) {
            sender.sendMessage(
                Message.HELP_ENTRY,
                TextReplacement("command", command),
                TextReplacement("description", description)
            )
        }
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        if (!invocation.source().hasPermission(Permissions.Commands.BEV)) return emptyList()
        val lastArg = invocation.arguments().lastOrNull()?.lowercase() ?: ""
        return listOf("reload", "help").filter { it.startsWith(lastArg) }
    }

    override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> =
        CompletableFuture.completedFuture(suggest(invocation))
}
