package dev.lenvx.betterenhancedvelocity.util

class PlayerNotFoundException(val playerName: String) : Exception("Player not found: $playerName")
