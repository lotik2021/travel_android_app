package ru.movista

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.movista.utils.durationBetweenTime

class DateConverter {

    @Test
    fun duration_between_time() {
        val result = durationBetweenTime("", "13:00")
        assertEquals("", result)

        assertEquals("30 мин", durationBetweenTime("12:00", "12:30"))

        assertEquals("< 1 мин", durationBetweenTime("12:00", "12:00"))

        assertEquals("", durationBetweenTime("12:00", "11:00"))
    }
}