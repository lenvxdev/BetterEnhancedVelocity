package dev.lenvx.betterenhancedvelocity.api

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class VanishManagerTest {

    private val registered = mutableListOf<VanishHook>()

    private fun hook(vararg vanished: UUID): VanishHook {
        val h = mockk<VanishHook>()
        every { h.isVanished(any()) } answers { firstArg<UUID>() in vanished }
        VanishManager.register(h)
        registered.add(h)
        return h
    }

    @AfterEach
    fun cleanup() {
        registered.forEach { VanishManager.unregister(it) }
        registered.clear()
    }

    @Test
    fun `isVanished returns false with no hooks`() {
        assertFalse(VanishManager.isVanished(UUID.randomUUID()))
    }

    @Test
    fun `isVanished returns true when hook marks player vanished`() {
        val uuid = UUID.randomUUID()
        hook(uuid)
        assertTrue(VanishManager.isVanished(uuid))
    }

    @Test
    fun `isVanished returns false when hook does not mark player vanished`() {
        hook()
        assertFalse(VanishManager.isVanished(UUID.randomUUID()))
    }

    @Test
    fun `isVanished returns true when any hook marks player vanished`() {
        val uuid = UUID.randomUUID()
        hook()
        hook(uuid)
        assertTrue(VanishManager.isVanished(uuid))
    }

    @Test
    fun `unregister removes hook`() {
        val uuid = UUID.randomUUID()
        val h = hook(uuid)
        VanishManager.unregister(h)
        registered.remove(h)
        assertFalse(VanishManager.isVanished(uuid))
    }

    @Test
    fun `getNonVanishedPlayers filters vanished players`() {
        val visibleUuid = UUID.randomUUID()
        val vanishedUuid = UUID.randomUUID()
        hook(vanishedUuid)

        val visible = mockk<com.velocitypowered.api.proxy.Player>()
        val vanished = mockk<com.velocitypowered.api.proxy.Player>()
        every { visible.uniqueId } returns visibleUuid
        every { vanished.uniqueId } returns vanishedUuid

        val server = mockk<com.velocitypowered.api.proxy.ProxyServer>()
        every { server.allPlayers } returns listOf(visible, vanished)

        val result = VanishManager.getNonVanishedPlayers(server)
        assertEquals(listOf(visible), result)
    }

    @Test
    fun `hasVanishedPlayer returns false when no players vanished`() {
        val uuid = UUID.randomUUID()
        hook()

        val player = mockk<com.velocitypowered.api.proxy.Player>()
        every { player.uniqueId } returns uuid

        val registeredServer = mockk<com.velocitypowered.api.proxy.server.RegisteredServer>()
        every { registeredServer.playersConnected } returns listOf<com.velocitypowered.api.proxy.Player>(player)

        assertFalse(VanishManager.hasVanishedPlayer(registeredServer))
    }

    @Test
    fun `hasVanishedPlayer returns true when a player is vanished`() {
        val uuid = UUID.randomUUID()
        hook(uuid)

        val player = mockk<com.velocitypowered.api.proxy.Player>()
        every { player.uniqueId } returns uuid

        val registeredServer = mockk<com.velocitypowered.api.proxy.server.RegisteredServer>()
        every { registeredServer.playersConnected } returns listOf<com.velocitypowered.api.proxy.Player>(player)

        assertTrue(VanishManager.hasVanishedPlayer(registeredServer))
    }
}
