package com.chooloo.www.chooloolib.interactor.calls

import com.chooloo.www.chooloolib.data.model.Call
import com.chooloo.www.chooloolib.data.model.Call.State
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class CallsInteractorImplTest {
    private val interactor = CallsInteractorImpl()
    private var nextId = 0

    private fun call(state: State, parent: Call? = null) = mockk<Call>(relaxed = true) {
        every { id } returns "c${nextId++}"
        every { this@mockk.state } returns state
        every { parentCall } returns parent
    }

    @Test
    fun noCalls() {
        assertThat(interactor.mainCall).isNull()
    }

    @Test
    fun singleCallIsMain() {
        val a = call(State.HOLDING)
        interactor.entryAddCall(a)
        assertThat(interactor.mainCall).isSameInstanceAs(a)
    }

    @Test
    fun conferenceChildResolvesToParent() {
        val parent = call(State.ACTIVE)
        interactor.entryAddCall(call(State.ACTIVE, parent))
        assertThat(interactor.mainCall).isSameInstanceAs(parent)
    }

    @Test
    fun mainCallPriority() {
        val holding = call(State.HOLDING)
        val active = call(State.ACTIVE)
        val incoming = call(State.INCOMING)
        val dialing = call(State.DIALING)
        interactor.entryAddCall(holding)
        interactor.entryAddCall(active)
        assertThat(interactor.mainCall).isSameInstanceAs(active)
        interactor.entryAddCall(incoming)
        assertThat(interactor.mainCall).isSameInstanceAs(incoming)
        interactor.entryAddCall(dialing)
        assertThat(interactor.mainCall).isSameInstanceAs(dialing)
    }

    @Test
    fun removeReleasesCallAndReportsNoCalls() {
        val a = call(State.ACTIVE)
        val listener = mockk<CallsInteractor.Listener>(relaxed = true)
        interactor.registerListener(listener)
        interactor.entryAddCall(a)
        interactor.entryRemoveCall(a)
        verify { a.release() }
        verify { listener.onNoCalls() }
        assertThat(interactor.mainCall).isNull()
    }
}
