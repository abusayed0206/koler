package com.chooloo.www.chooloolib.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {
    @Test
    fun initialsTakesFirstLetterOfEachWord() = assertEquals("JD", "John Doe".initials())

    @Test
    fun initialsOfBlankNameIsEmptyInsteadOfCrashing() {
        assertEquals("", "".initials())
        assertEquals("", " ".initials())
    }

    @Test
    fun initialsIgnoresRepeatedSpaces() = assertEquals("AB", "A  B".initials())

    @Test
    fun isRtlDetectsFirstCharDirection() {
        assertEquals(false, "".isRTL())
        assertEquals(false, "John".isRTL())
        assertEquals(true, "\u05D0\u05D1".isRTL()) // Hebrew
        assertEquals(true, "\u0645\u062D".isRTL()) // Arabic
    }
}
