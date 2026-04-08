package dev.lenvx.betterenhancedvelocity.api

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import java.util.UUID

interface VanishHook {
    fun isVanished(uuid: UUID): Boolean
    fun vanish(uuid: UUID)
    fun unvanish(uuid: UUID)
}

object VanishManager {
    private val hooks = mutableListOf<VanishHook>()

    fun register(hook: VanishHook) = hooks.add(hook)
    fun unregister(hook: VanishHook) = hooks.remove(hook)

    fun isVanished(uuid: UUID): Boolean = hooks.any { it.isVanished(uuid) }

    fun hasVanishedPlayer(server: RegisteredServer): Boolean =
        server.playersConnected.any { isVanished(it.uniqueId) }

    fun getNonVanishedPlayers(server: ProxyServer): List<Player> =
        server.allPlayers.filterNot { isVanished(it.uniqueId) }

    fun getNonVanishedPlayers(registeredServer: RegisteredServer): List<Player> =
        registeredServer.playersConnected.filterNot { isVanished(it.uniqueId) }
}
