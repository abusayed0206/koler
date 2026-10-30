package com.chooloo.www.chooloolib.interactor.dialog

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UniqueLabelsTest {
    @Test
    fun distinctLabelsAreUnchanged() {
        assertThat(uniqueLabels(listOf("SIM 1", "SIM 2"))).containsExactly("SIM 1", "SIM 2").inOrder()
    }

    @Test
    fun sameLabelledSimsStayTwoChoices() {
        assertThat(uniqueLabels(listOf("Carrier", "Carrier", "Other")))
            .containsExactly("Carrier (1)", "Carrier (2)", "Other").inOrder()
    }
}
