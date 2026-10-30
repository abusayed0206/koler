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
}
