package dev.lenvx.betterenhancedvelocity.config

import dev.lenvx.betterenhancedvelocity.model.ServerData
import dev.lenvx.betterenhancedvelocity.util.ResourceUtils
import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path

object Settings {

    private val messages = mutableMapOf<Message, String>()
    val servers = mutableMapOf<String, ServerData>()

    var defaultLanguage: String = "en_US"

    var glistEnabled: Boolean = true
    var globalListCommand: String = "glist"
    var globalListAliases: List<String> = emptyList()
    var globalListMaxServers: Int = 9
    var progressCount: Int = 45
    var progressComplete: String = "|"
    var progressNotComplete: String = "<gray>|"
    var playerVanishDecoration: String = "<red>\$player</red>"
    var serverVanishDecoration: String = "<yellow>\$server</yellow>"

    var findEnabled: Boolean = true
    var findCommand: String = "find"
    var findAliases: List<String> = emptyList()

    var sendEnabled: Boolean = true
    var sendCommand: String = "send"
    var sendAliases: List<String> = emptyList()

    var alertEnabled: Boolean = true
    var alertCommand: String = "alert"
    var alertAliases: List<String> = emptyList()

    var broadcastEnabled: Boolean = true
    var broadcastCommand: String = "broadcast"
    var broadcastAliases: List<String> = emptyList()

    var pingEnabled: Boolean = true
    var pingCommand: String = "ping"
    var pingAliases: List<String> = emptyList()

    var kickAllEnabled: Boolean = true
    var kickAllCommand: String = "kickall"
    var kickAllAliases: List<String> = emptyList()

    var moveEnabled: Boolean = true
    var moveCommand: String = "move"
    var moveAliases: List<String> = emptyList()

    var startupCommands: List<String> = emptyList()

    fun load(dataDirectory: Path) {
        val settingsRoot = YamlConfig(dataDirectory, "settings.yml").load()

        defaultLanguage = settingsRoot.node("default_language").getString("en_US")

        val features = settingsRoot.node("features")

        val glist = features.node("global_list")
        glistEnabled = glist.node("enabled").getBoolean(true)
        globalListCommand = glist.node("command").getString("glist")
        globalListAliases = glist.node("aliases").getList(String::class.java) ?: emptyList()
        globalListMaxServers = glist.node("max-servers").getInt(9)

        val progress = glist.node("progress")
        progressCount = progress.node("count").getInt(45)
        progressComplete = progress.node("complete").getString("|")
        progressNotComplete = progress.node("not_complete").getString("<gray>|")

        val vanish = glist.node("vanish", "decoration")
        playerVanishDecoration = vanish.node("player").getString("<red>\$player</red>")
        serverVanishDecoration = vanish.node("server").getString("<yellow>\$server</yellow>")

        servers.clear()
        for ((key, value) in glist.node("server").childrenMap()) {
            servers[key.toString()] = ServerData(
                displayname = value.node("displayname").string,
                summarizedServers = value.node("sum").getList(String::class.java),
                hidden = value.node("hidden").getBoolean(false)
            )
        }

        val find = features.node("find")
        findEnabled = find.node("enabled").getBoolean(true)
        findCommand = find.node("command").getString("find")
        findAliases = find.node("aliases").getList(String::class.java) ?: emptyList()

        val send = features.node("send")
        sendEnabled = send.node("enabled").getBoolean(true)
        sendCommand = send.node("command").getString("send")
        sendAliases = send.node("aliases").getList(String::class.java) ?: emptyList()

        val alert = features.node("alert")
        alertEnabled = alert.node("enabled").getBoolean(true)
        alertCommand = alert.node("command").getString("alert")
        alertAliases = alert.node("aliases").getList(String::class.java) ?: emptyList()

        val broadcast = features.node("broadcast")
        broadcastEnabled = broadcast.node("enabled").getBoolean(true)
        broadcastCommand = broadcast.node("command").getString("broadcast")
        broadcastAliases = broadcast.node("aliases").getList(String::class.java) ?: emptyList()

        val ping = features.node("ping")
        pingEnabled = ping.node("enabled").getBoolean(true)
        pingCommand = ping.node("command").getString("ping")
        pingAliases = ping.node("aliases").getList(String::class.java) ?: emptyList()

        val kickAll = features.node("kickall")
        kickAllEnabled = kickAll.node("enabled").getBoolean(true)
        kickAllCommand = kickAll.node("command").getString("kickall")
        kickAllAliases = kickAll.node("aliases").getList(String::class.java) ?: emptyList()

        val move = features.node("move")
        moveEnabled = move.node("enabled").getBoolean(true)
        moveCommand = move.node("command").getString("move")
        moveAliases = move.node("aliases").getList(String::class.java) ?: emptyList()

        startupCommands = settingsRoot.node("startup_commands").getList(String::class.java) ?: emptyList()

        val languageRoot = YamlConfig(dataDirectory, "languages/$defaultLanguage.yml", overwrite = true).load()
        messages.clear()
        for (message in Message.entries) {
            if (message == Message.EMPTY) {
                messages[message] = ""
                continue
            }
            messages[message] = languageRoot.node(message.path.split(".")).string
                ?: "Missing message: ${message.name}"
        }
    }

    fun formatMessage(message: String, vararg replacements: TextReplacement): String {
        var result = message
            .replace("\$prefix", getMessage(Message.PREFIX))
            .replace("\$successful_prefix", getMessage(Message.SUCCESSFUL_PREFIX))
            .replace("\$warn_prefix", getMessage(Message.WARN_PREFIX))
            .replace("\$error_prefix", getMessage(Message.ERROR_PREFIX))
        for (r in replacements) {
            result = result.replace("\$${r.from}", r.to)
        }
        return result
    }

    fun formatMessage(message: Message, vararg replacements: TextReplacement): String =
        formatMessage(getMessage(message), *replacements)

    fun formatMessage(messageList: List<String>, vararg replacements: TextReplacement): List<String> =
        messageList.map { formatMessage(it, *replacements) }

    private fun getMessage(message: Message): String =
        messages[message] ?: "Unknown message (${message.name})"

    private class YamlConfig(private val dataDirectory: Path, private val filePath: String, private val overwrite: Boolean = false) {
        private val file: File = dataDirectory.resolve(filePath).toFile()
        private val loader: YamlConfigurationLoader = YamlConfigurationLoader.builder().file(file).build()

        fun load(): CommentedConfigurationNode {
            if (!file.exists() || overwrite) {
                file.parentFile?.mkdirs()
                ResourceUtils.copyResource(filePath, file)
            }
            return loader.load()
        }
    }
}
