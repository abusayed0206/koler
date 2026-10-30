package com.chooloo.www.chooloolib.data.model

import android.telecom.Call.Details
import android.telecom.Call.Details.DIRECTION_INCOMING
import android.telecom.Call.Details.DIRECTION_OUTGOING
import android.telecom.Call.Details.DIRECTION_UNKNOWN
import android.telecom.DisconnectCause
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CallTest {
    private fun telecom(
        state: Int = android.telecom.Call.STATE_ACTIVE,
        direction: Int = DIRECTION_UNKNOWN,
    ): android.telecom.Call {
        val details = mockk<Details>(relaxed = true) {
            every { this@mockk.state } returns state
            every { callDirection } returns direction
            every { gatewayInfo } returns null
            every { extras } returns null
            every { intentExtras } returns null
            every { accountHandle } returns null
            every { disconnectCause } returns null
            every { handle } returns null
        }
        return mockk(relaxed = true) { every { this@mockk.details } returns details }
    }

    @Test
    fun equalityAndHashCodeFollowTelecomCall() {
        val t = telecom()
        val a = Call(t)
        val b = Call(t)
        assertThat(a.id).isNotEqualTo(b.id)
        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
        assertThat(setOf(a, b)).hasSize(1)
        assertThat(Call(telecom())).isNotEqualTo(a)
    }

    @Test
    fun fromIsCanonicalAndRegistersOnce() {
        val t = telecom()
        val a = Call.from(t)
        assertThat(Call.from(t)).isSameInstanceAs(a)
        verify(exactly = 1) { t.registerCallback(any()) }

        a.release()
        verify(exactly = 1) { t.unregisterCallback(any()) }
        val b = Call.from(t)
        assertThat(b).isNotSameInstanceAs(a)
        b.release()
    }

    @Test
    fun directionFromTelecomWhenKnown() {
        assertThat(Call(telecom(direction = DIRECTION_OUTGOING)).isDirectionOutgoing).isTrue()
        assertThat(
            Call(telecom(android.telecom.Call.STATE_RINGING, DIRECTION_INCOMING)).isDirectionIncoming
        ).isTrue()
    }

    @Test
    fun directionFallsBackToInitialState() {
        // onCallAdded: no state change has been reported yet
        for (state in listOf(
            android.telecom.Call.STATE_DIALING,
            android.telecom.Call.STATE_CONNECTING,
            android.telecom.Call.STATE_SELECT_PHONE_ACCOUNT,
        )) {
            assertThat(Call(telecom(state)).isDirectionOutgoing).isTrue()
        }
        val incoming = Call(telecom(android.telecom.Call.STATE_RINGING))
        assertThat(incoming.isDirectionIncoming).isTrue()
        assertThat(incoming.isDirectionOutgoing).isFalse()
    }

    @Test
    fun stateIsRecordedBeforeListenersAreNotified() {
        val t = telecom(android.telecom.Call.STATE_NEW)
        val callback = slot<android.telecom.Call.Callback>()
        every { t.registerCallback(capture(callback)) } returns Unit
        val call = Call.from(t)
        var outgoingSeen: Boolean? = null
        call.registerListener(object : Call.Listener {
            override fun onCallChanged(call: Call) {
                outgoingSeen = call.isDirectionOutgoing
            }
        })
        callback.captured.onStateChanged(t, android.telecom.Call.STATE_DIALING)
        assertThat(outgoingSeen).isTrue()
        call.release()
    }

    @Test
    fun nullTelecomDetailsAreSafe() {
        val call = Call(telecom(android.telecom.Call.STATE_DISCONNECTED))
        assertThat(call.number).isNull()
        assertThat(call.callSubject).isNull()
        assertThat(call.phoneAccountHandle).isNull()
        assertThat(call.availablePhoneAccounts).isEmpty()
        assertThat(call.suggestedPhoneAccounts).isEmpty()
        assertThat(call.disconnectCause.code).isEqualTo(DisconnectCause.UNKNOWN)
    }
}
