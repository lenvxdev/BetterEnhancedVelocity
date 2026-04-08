package dev.lenvx.betterenhancedvelocity.config

import dev.lenvx.betterenhancedvelocity.util.TextReplacement

enum class Message(val path: String) {
    RAW_PREFIX("general.raw_prefix"),
    PREFIX("general.prefix"),
    CONSOLE_PREFIX("general.console_prefix"),
    SUCCESSFUL_PREFIX("general.successful_prefix"),
    WARN_PREFIX("general.warn_prefix"),
    ERROR_PREFIX("general.error_prefix"),
    ONLY_PLAYERS("general.only_players"),
    VALID_PARAMS("general.valid_parameters"),
    PLAYER_NOT_FOUND("general.player_not_found"),
    SERVER_NOT_FOUND("general.server_not_found"),
    NO_PERMISSION("command.no_permission"),
    GLOBALLIST_HEADER("features.global_list.header"),
    NO_ONE_PLAYING("features.global_list.no_one_playing"),
    GLOBALLIST_SERVER("features.global_list.server"),
    FIND_USAGE("features.find.command.usage"),
    FIND_USE("features.find.command.use"),
    FIND_VANISHED("features.find.command.vanished"),
    FIND_NO_SERVER("features.find.command.no_server"),
    FIND_NO_TARGET("features.find.command.no_target"),
    SEND_USAGE("features.send.command.usage"),
    SEND_USE("features.send.command.use"),
    ALERT_USAGE("features.alert.command.usage"),
    ALERT_USE("features.alert.command.use"),
    BROADCAST_USAGE("features.broadcast.command.usage"),
    BROADCAST_USE("features.broadcast.command.use"),
    PING_USE("features.ping.command.use"),
    PING_NO_TARGET("features.ping.command.no_target"),
    PING_USE_TARGET("features.ping.command.use_target"),
    KICKALL_USAGE("features.kickall.command.usage"),
    KICKALL_USE("features.kickall.command.use"),
    KICKALL_NO_SERVER("features.kickall.command.no_server"),
    KICKALL_REASON("features.kickall.command.reason"),
    MOVE_USAGE("features.move.command.usage"),
    MOVE_SUCCESS("features.move.command.success"),
    MOVE_NO_PLAYERS_FOUND("features.move.command.no_players_found"),
    MOVE_ALREADY_ON_SERVER("features.move.command.already_on_server"),
    MOVE_FAILED("features.move.command.failed"),
    BEV_RELOAD_SUCCESS("features.bev.command.reload_success"),
    BEV_USAGE("features.bev.command.usage"),
    HELP_HEADER("features.help.command.header"),
    HELP_PLUGIN_HEADER("features.help.command.plugin_header"),
    HELP_ENTRY("features.help.command.entry"),
    HELP_DESC_GLIST("features.help.descriptions.glist"),
    HELP_DESC_FIND("features.help.descriptions.find"),
    HELP_DESC_SEND("features.help.descriptions.send"),
    HELP_DESC_ALERT("features.help.descriptions.alert"),
    HELP_DESC_BROADCAST("features.help.descriptions.broadcast"),
    HELP_DESC_PING("features.help.descriptions.ping"),
    HELP_DESC_KICKALL("features.help.descriptions.kickall"),
    HELP_DESC_MOVE("features.help.descriptions.move"),
    HELP_DESC_BEV("features.help.descriptions.bev"),
    EMPTY("");

    fun get(vararg replacements: TextReplacement): String =
        Settings.formatMessage(this, *replacements)

    override fun toString(): String = get()
}
