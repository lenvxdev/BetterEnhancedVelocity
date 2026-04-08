package dev.lenvx.betterenhancedvelocity.config

import dev.lenvx.betterenhancedvelocity.util.TextReplacement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingsFormatMessageTest {

    @Test
    fun `replaces single placeholder`() {
        val result = Settings.formatMessage("Hello \$player!", TextReplacement("player", "Leo"))
        assertEquals("Hello Leo!", result)
    }

    @Test
    fun `replaces multiple placeholders`() {
        val result = Settings.formatMessage(
            "\$player joined \$server",
            TextReplacement("player", "Leo"),
            TextReplacement("server", "lobby")
        )
        assertEquals("Leo joined lobby", result)
    }

    @Test
    fun `returns message unchanged when no placeholders`() {
        val result = Settings.formatMessage("No placeholders here")
        assertEquals("No placeholders here", result)
    }

    @Test
    fun `placeholder with no matching replacement is left untouched`() {
        val result = Settings.formatMessage("Hello \$player", TextReplacement("server", "lobby"))
        assertEquals("Hello \$player", result)
    }

    @Test
    fun `replaces placeholder that appears multiple times`() {
        val result = Settings.formatMessage("\$x and \$x", TextReplacement("x", "A"))
        assertEquals("A and A", result)
    }

    @Test
    fun `replacement value can be empty string`() {
        val result = Settings.formatMessage("Hello\$suffix!", TextReplacement("suffix", ""))
        assertEquals("Hello!", result)
    }

    @Test
    fun `formatMessage on list applies to each entry`() {
        val result = Settings.formatMessage(
            listOf("Welcome \$player", "Server: \$server"),
            TextReplacement("player", "Leo"),
            TextReplacement("server", "lobby")
        )
        assertEquals(listOf("Welcome Leo", "Server: lobby"), result)
    }
}
