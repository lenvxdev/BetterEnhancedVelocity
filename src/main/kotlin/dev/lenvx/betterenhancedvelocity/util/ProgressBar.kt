package dev.lenvx.betterenhancedvelocity.util

fun progressBar(current: Int, max: Int, total: Int, complete: String, notComplete: String): String {
    if (max == 0) return notComplete.repeat(total)
    val filled = (total * (current.toFloat() / max)).toInt()
    return complete.repeat(filled) + notComplete.repeat(total - filled)
}
