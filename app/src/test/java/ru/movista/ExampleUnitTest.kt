package ru.movista

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.movista.utils.round

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun double_round() {
        assertEquals(-3.1, -3.1.round(2), 0.0)
        assertEquals(-0.0, -0.0.round(2), 0.0)

    }
}
