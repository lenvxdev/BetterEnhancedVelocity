package dev.lenvx.betterenhancedvelocity.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProgressBarTest {

    private val complete = "|"
    private val empty = "-"

    @Test
    fun `all empty when max is zero`() {
        assertEquals("----------", progressBar(0, 0, 10, complete, empty))
    }

    @Test
    fun `all empty when current is zero`() {
        assertEquals("----------", progressBar(0, 100, 10, complete, empty))
    }

    @Test
    fun `all filled when current equals max`() {
        assertEquals("||||||||||", progressBar(100, 100, 10, complete, empty))
    }

    @Test
    fun `half filled when current is half of max`() {
        assertEquals("|||||-----", progressBar(50, 100, 10, complete, empty))
    }

    @Test
    fun `correct total length always`() {
        for (current in 0..10) {
            val bar = progressBar(current, 10, 20, complete, empty)
            assertEquals(20, bar.length, "bar length mismatch at current=$current")
        }
    }

    @Test
    fun `fills proportionally`() {
        val bar = progressBar(1, 4, 4, complete, empty)
        assertEquals("|---", bar)
        assertEquals(1, bar.count { it == '|' })
        assertEquals(3, bar.count { it == '-' })
    }

    @Test
    fun `multi-char tokens work`() {
        val bar = progressBar(10, 10, 3, "##", "..")
        assertEquals("######", bar)
    }

    @Test
    fun `single slot fully filled`() {
        assertEquals(complete, progressBar(1, 1, 1, complete, empty))
    }

    @Test
    fun `single slot empty`() {
        assertEquals(empty, progressBar(0, 1, 1, complete, empty))
    }
}
