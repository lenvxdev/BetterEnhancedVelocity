package dev.lenvx.betterenhancedvelocity.command

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.api.VanishManager
import dev.lenvx.betterenhancedvelocity.config.Message
import dev.lenvx.betterenhancedvelocity.config.Settings
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import dev.lenvx.betterenhancedvelocity.util.progressBar
import dev.lenvx.betterenhancedvelocity.util.sendMessage

class GListCommand(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()

        if (!sender.hasPermission(Permissions.Commands.GLIST)) {
            sender.sendMessage(Message.NO_PERMISSION, TextReplacement("permission", Permissions.Commands.GLIST))
            return
        }

        val canSeeVanished = sender.hasPermission(Permissions.Actions.SEE_VANISHED)
        val onlinePlayers = if (canSeeVanished) server.allPlayers else VanishManager.getNonVanishedPlayers(server)

        sender.sendMessage(Message.GLOBALLIST_HEADER, TextReplacement("count", onlinePlayers.size.toString()))

        server.allServers
            .map { s ->
                val players = s.playersConnected.filter { canSeeVanished || !VanishManager.isVanished(it.uniqueId) }
                s to players
            }
            .filterNot { (s, _) -> Settings.servers[s.serverInfo.name]?.hidden == true }
            .sortedByDescending { (_, players) -> players.size }
            .take(Settings.globalListMaxServers)
            .forEach { (s, players) ->
                val displayName = Settings.servers[s.serverInfo.name]?.displayname ?: s.serverInfo.name
                val progress = progressBar(
                    players.size, onlinePlayers.size,
                    Settings.progressCount, Settings.progressComplete, Settings.progressNotComplete
                )
                val playersString = if (players.isEmpty()) {
                    Settings.formatMessage(Message.NO_ONE_PLAYING)
                } else {
                    formatPlayerList(players, canSeeVanished)
                }
                sender.sendMessage(
                    Message.GLOBALLIST_SERVER,
                    TextReplacement("players", playersString),
                    TextReplacement("progress", progress),
                    TextReplacement("count", players.size.toString()),
                    TextReplacement("server", displayName)
                )
            }
    }

    private fun formatPlayerList(players: Collection<Player>, canSeeVanished: Boolean): String =
        players.joinToString(", ") { player ->
            if (canSeeVanished && VanishManager.isVanished(player.uniqueId)) {
                Settings.formatMessage(Settings.playerVanishDecoration.replace("\$player", player.username))
            } else {
                player.username
            }
        }
}
