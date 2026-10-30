package com.chooloo.www.chooloolib.util

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SelectionBuilderTest {
    @Test
    fun valuesGoToArgsNotSelection() {
        val builder = SelectionBuilder().addNotNull("name").addSelection("_id", 5L)
        assertEquals("name IS NOT NULL AND _id = ?", builder.build())
        assertArrayEquals(arrayOf("5"), builder.buildArgs())
    }

    @Test
    fun nullValuesAreSkipped() {
        val builder = SelectionBuilder().addSelection("_id", null).addContains(listOf("name"), null)
        assertEquals("", builder.build())
        assertArrayEquals(arrayOf<String>(), builder.buildArgs())
    }

    @Test
    fun containsUsesPlaceholdersPerKey() {
        val builder = SelectionBuilder().addContains(listOf("name", "number"), "O'Brien")
        assertEquals("(name LIKE ? ESCAPE '\\' OR number LIKE ? ESCAPE '\\')", builder.build())
        assertArrayEquals(arrayOf("%O'Brien%", "%O'Brien%"), builder.buildArgs())
    }

    @Test
    fun argsFollowSelectionOrder() {
        val builder = SelectionBuilder().addSelection("_id", 1).addContains(listOf("name"), "a")
        assertEquals("_id = ? AND (name LIKE ? ESCAPE '\\')", builder.build())
        assertArrayEquals(arrayOf("1", "%a%"), builder.buildArgs())
    }

    @Test
    fun escapeLikeEscapesWildcardsAndEscapeChar() {
        assertEquals("50\\%", SelectionBuilder.escapeLike("50%"))
        assertEquals("a\\_b", SelectionBuilder.escapeLike("a_b"))
        assertEquals("c:\\\\d", SelectionBuilder.escapeLike("c:\\d"))
        assertEquals("\\\\\\%", SelectionBuilder.escapeLike("\\%"))
        assertEquals("O'Brien", SelectionBuilder.escapeLike("O'Brien"))
    }
}
