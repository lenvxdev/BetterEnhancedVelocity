# BetterEnhancedVelocity

[![GitHub Release](https://img.shields.io/github/v/release/lenvxdev/BetterEnhancedVelocity?style=flat-square&color=4C9BE8)](https://github.com/lenvxdev/BetterEnhancedVelocity/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/betterenchantedvelocity?style=flat-square&color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/betterenchantedvelocity)
[![License](https://img.shields.io/github/license/lenvxdev/BetterEnhancedVelocity?style=flat-square&color=red)](LICENSE)
[![Velocity](https://img.shields.io/badge/Velocity-3.x-blue?style=flat-square)](https://velocitypowered.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)

A Velocity proxy plugin for efficient network management. Provides configurable commands for player and server administration, with full vanish plugin support and multi-language messages.

## Installation

1. Download the latest JAR from [GitHub Releases](https://github.com/lenvxdev/BetterEnhancedVelocity/releases) or [Modrinth](https://modrinth.com/plugin/betterenchantedvelocity)
2. Drop it into your Velocity `plugins/` folder
3. Restart the proxy
4. Edit `plugins/betterenhancedvelocity/settings.yml` to configure features and commands

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/glist` | List all players across every server with a progress bar | `betterenhancedvelocity.commands.glist` |
| `/find <player>` | Show which server a player is on | `betterenhancedvelocity.commands.find` |
| `/ping [player]` | Check your ping or another player's | `betterenhancedvelocity.commands.ping` |
| `/send <player> <server>` | Send a player to a server | `betterenhancedvelocity.commands.send` |
| `/alert <message>` | Broadcast a message to all players | `betterenhancedvelocity.commands.alert` |
| `/kickall <server\|all>` | Kick all players from a server or the entire proxy | `betterenhancedvelocity.commands.kickall` |
| `/move <player\|server:\<name\>\|all> <dest>` | Move players to a destination server | `betterenhancedvelocity.commands.move` |
| `/bev reload` | Reload config and language files | `betterenhancedvelocity.admin` |

### Action Permissions

| Permission | Description |
|------------|-------------|
| `betterenhancedvelocity.actions.seevanished` | See vanished players in commands |
| `betterenhancedvelocity.actions.findvanished` | See vanished status in `/find` |
| `betterenhancedvelocity.actions.kickall.bypass` | Exempt from `/kickall` |
| `betterenhancedvelocity.actions.move.all` | Use `/move all` |
| `betterenhancedvelocity.actions.move.server` | Use `/move server:<name>` |
| `betterenhancedvelocity.actions.move.player` | Use `/move <player>` |

## Configuration

All features can be individually enabled or disabled in `settings.yml`:

```yaml
features:
  global_list:
    enabled: true   # set to false to disable /glist entirely
    command: glist
    aliases: [globallist, list, players]
  find:
    enabled: true
  send:
    enabled: true
  alert:
    enabled: true
  ping:
    enabled: true
  kickall:
    enabled: true
  move:
    enabled: true
```

Command names and aliases are configurable per feature.

Startup commands run automatically when the proxy initializes:

```yaml
startup_commands:
  - "alert Proxy has started!"
```

### Global List Server Groups

The `/glist` command supports grouping sub-servers under a single display entry, hiding servers from the list, and custom display names:

```yaml
features:
  global_list:
    server:
      # This entry groups bw-1, bw-2, bw-3 and shows them as one "BedWars" row
      BedWars:
        displayname: "BedWars"
        sum:
          - "bw-1"
          - "bw-2"
          - "bw-3"
      # These are hidden from the list since they appear under BedWars above
      bw-1:
        hidden: true
      bw-2:
        hidden: true
      bw-3:
        hidden: true
```

`displayname` overrides the server name shown in the list. `sum` merges the player counts of the listed servers into this row. `hidden: true` removes a server from the output entirely.

## Languages

Two languages are bundled: `en_US` and `tr_TR`. To switch, set `default_language` in `settings.yml`:

```yaml
default_language: "tr_TR"
```

To add a custom language, copy `plugins/betterenhancedvelocity/languages/en_US.yml` to a new file (e.g. `de_DE.yml`), translate the values, then set `default_language: "de_DE"`. Run `/bev reload` to apply without restarting.

All messages support [MiniMessage](https://docs.advntr.dev/minimessage/format.html) formatting.

## Vanish Integration

BetterEnhancedVelocity has a built-in vanish API. If you have a vanish plugin, implement the `VanishHook` interface and register it so vanished players are hidden correctly across all commands:

```kotlin
import dev.lenvx.betterenhancedvelocity.api.VanishHook
import dev.lenvx.betterenhancedvelocity.api.VanishManager
import java.util.UUID

class MyVanishHook : VanishHook {
    override fun isVanished(uuid: UUID): Boolean = MyVanishPlugin.isVanished(uuid)
    override fun vanish(uuid: UUID) = MyVanishPlugin.vanish(uuid)
    override fun unvanish(uuid: UUID) = MyVanishPlugin.unvanish(uuid)
}

// Register on plugin enable
VanishManager.register(MyVanishHook())
```

Without a registered hook, all players are treated as visible.

## Requirements

- Velocity 3.x
- Java 17+

## Building

```bash
./gradlew build
```

The plugin JAR will be in `/bin/BetterEnhancedVelocity v<version>.jar`.

## Contributing

Pull requests are welcome. For bugs, open an issue on GitHub and include exact steps to reproduce, your Velocity version, and any relevant logs.

## Credits

Original [EnhancedVelocity](https://github.com/syrent/enhancedvelocity) plugin by [Syrent](https://github.com/syrent).
