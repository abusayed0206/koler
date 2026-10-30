package com.chooloo.www.chooloolib.data.model

import com.chooloo.www.chooloolib.data.model.Call.State
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Robolectric only for removeFallsBackToTelecomCallEquality, which builds real Calls
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CallListTest {
    private val list = CallList()

    private fun call(
        id: String,
        state: State = State.ACTIVE,
        telecom: android.telecom.Call = mockk(),
        conference: Boolean = false,
        inConference: Boolean = false,
        conferenceParent: Boolean = false,
    ) = mockk<Call> {
        every { this@mockk.id } returns id
        every { this@mockk.state } returns state
        every { telecomCall } returns telecom
        every { isConference } returns conference
        every { isInConference } returns inConference
        every { isConferenceParent } returns conferenceParent
        every { isActive } returns (state == State.ACTIVE)
        every { isHolding } returns (state == State.HOLDING)
    }

    @Test
    fun updateAddsAndReplacesById() {
        list.update(call("1"))
        list.update(call("2"))
        val replacement = call("1", State.HOLDING)
        list.update(replacement)
        assertThat(list.size).isEqualTo(2)
        assertThat(list["1"]).isSameInstanceAs(replacement)
        assertThat(list["3"]).isNull()
        assertThat(replacement in list).isTrue()
    }

    @Test
    fun removeById() {
        val a = call("1")
        list.update(a)
        list.update(call("2"))
        list.remove(a)
        assertThat(list.size).isEqualTo(1)
        assertThat(list["1"]).isNull()
    }

    @Test
    fun removeFallsBackToTelecomCallEquality() {
        val telecom = mockk<android.telecom.Call>(relaxed = true)
        list.update(Call(telecom))
        list.remove(Call(mockk(relaxed = true))) // different telecom call: kept
        assertThat(list.size).isEqualTo(1)
        list.remove(Call(telecom)) // new id, same telecom call: removed
        assertThat(list.size).isEqualTo(0)
    }

    @Test
    fun stateQueries() {
        val active = call("1", State.ACTIVE)
        val holding = call("2", State.HOLDING)
        list.update(active)
        list.update(holding)
        assertThat(list.hasState(State.ACTIVE)).isTrue()
        assertThat(list.hasState(State.DIALING)).isFalse()
        assertThat(list.getState(State.HOLDING)).containsExactly(holding)
        assertThat(list.getFirstState(State.ACTIVE)).isSameInstanceAs(active)
        assertThat(list.getFirstState(State.INCOMING)).isNull()
        assertThat(list.hasStates(arrayOf(State.ACTIVE, State.HOLDING))).isTrue()
        assertThat(list.hasStates(arrayOf(State.ACTIVE, State.DIALING))).isFalse()
        assertThat(list.getNotStates(arrayOf(State.ACTIVE))).containsExactly(holding)
    }

    @Test
    fun getByTelecomCall() {
        val telecom = mockk<android.telecom.Call>()
        val a = call("1", telecom = telecom)
        list.update(a)
        list.update(call("2"))
        assertThat(list.getByTelecomCall(telecom)).isSameInstanceAs(a)
        assertThat(list.getByTelecomCall(mockk())).isNull()
    }

    @Test
    fun conferenceFilters() {
        val conference = call("1", conference = true, conferenceParent = true)
        val child = call("2", inConference = true)
        val single = call("3")
        listOf(conference, child, single).forEach(list::update)
        assertThat(list.conferenceCalls).containsExactly(conference)
        assertThat(list.conferenceParentCalls).containsExactly(conference)
        assertThat(list.nonConferenceCalls).containsExactly(single)
    }

    @Test
    fun getIndexInRange() {
        val a = call("1")
        list.update(a)
        assertThat(list.getIndex(0)).isSameInstanceAs(a)
    }

    @Ignore("bug: getIndex is declared Call? but throws IndexOutOfBoundsException out of range — fixed in Phase 3")
    @Test
    fun getIndexOutOfRangeIsNull() {
        assertThat(list.getIndex(0)).isNull()
    }

    @Ignore("bug: conferenceableCalls lists an active/holding conference parent twice — fixed in Phase 3")
    @Test
    fun conferenceableCallsHasNoDuplicates() {
        val parent = call("1", State.ACTIVE, conferenceParent = true)
        val held = call("2", State.HOLDING)
        list.update(parent)
        list.update(held)
        assertThat(list.conferenceableCalls).containsExactly(parent, held)
    }
}
