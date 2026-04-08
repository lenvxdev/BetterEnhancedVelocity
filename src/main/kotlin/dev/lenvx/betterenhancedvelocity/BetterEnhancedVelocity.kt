package dev.lenvx.betterenhancedvelocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.lenvx.betterenhancedvelocity.command.*
import dev.lenvx.betterenhancedvelocity.config.Settings
import org.bstats.velocity.Metrics
import org.slf4j.Logger
import java.nio.file.Path

class BetterEnhancedVelocity @Inject constructor(
    val server: ProxyServer,
    val logger: Logger,
    private val metricsFactory: Metrics.Factory,
    @DataDirectory val dataDirectory: Path
) {
    companion object {
        lateinit var instance: BetterEnhancedVelocity
            private set
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        instance = this
        Settings.load(dataDirectory)
        metricsFactory.make(this, 16753)
        registerCommands()
        executeStartupCommands()
        logger.info("BetterEnhancedVelocity enabled.")
    }

    fun reload() {
        Settings.load(dataDirectory)
        logger.info("Configuration reloaded.")
    }

    private fun registerCommands() {
        val cm = server.commandManager

        cm.register(cm.metaBuilder("bev").aliases("betterenhancedvelocity").build(), BevCommand(this))

        if (Settings.glistEnabled) {
            cm.register(
                cm.metaBuilder(Settings.globalListCommand)
                    .aliases(*Settings.globalListAliases.toTypedArray()).build(),
                GListCommand(server)
            )
        }

        if (Settings.findEnabled) {
            cm.register(
                cm.metaBuilder(Settings.findCommand)
                    .aliases(*Settings.findAliases.toTypedArray()).build(),
                FindCommand(server)
            )
        }

        if (Settings.sendEnabled) {
            cm.register(
                cm.metaBuilder(Settings.sendCommand)
                    .aliases(*Settings.sendAliases.toTypedArray()).build(),
                SendCommand(server)
            )
        }

        if (Settings.alertEnabled) {
            cm.register(
                cm.metaBuilder(Settings.alertCommand)
                    .aliases(*Settings.alertAliases.toTypedArray()).build(),
                AlertCommand(server)
            )
        }

        if (Settings.broadcastEnabled) {
            cm.register(
                cm.metaBuilder(Settings.broadcastCommand)
                    .aliases(*Settings.broadcastAliases.toTypedArray()).build(),
                BroadcastCommand(server)
            )
        }

        if (Settings.pingEnabled) {
            cm.register(
                cm.metaBuilder(Settings.pingCommand)
                    .aliases(*Settings.pingAliases.toTypedArray()).build(),
                PingCommand(server)
            )
        }

        if (Settings.kickAllEnabled) {
            cm.register(
                cm.metaBuilder(Settings.kickAllCommand)
                    .aliases(*Settings.kickAllAliases.toTypedArray()).build(),
                KickAllCommand(server)
            )
        }

        if (Settings.moveEnabled) {
            cm.register(
                cm.metaBuilder(Settings.moveCommand)
                    .aliases(*Settings.moveAliases.toTypedArray()).build(),
                MoveCommand(server)
            )
        }
    }

    private fun executeStartupCommands() {
        for (command in Settings.startupCommands) {
            server.commandManager.executeAsync(server.consoleCommandSource, command)
            logger.info("Executed startup command: $command")
        }
    }
}
