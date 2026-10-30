package com.chooloo.www.chooloolib.interactor.callaudio

import android.telecom.CallAudioState
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor.AudioRoute
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class CallAudiosInteractorImplTest {
    private val interactor = CallAudiosInteractorImpl()
    private val listener = mockk<CallAudiosInteractor.Listener>(relaxed = true)

    private fun state(muted: Boolean, route: Int) = mockk<CallAudioState> {
        every { isMuted } returns muted
        every { this@mockk.route } returns route
    }

    @Test
    fun firesOnlyWhatChanged() {
        interactor.registerListener(listener)
        interactor.entryCallAudioStateChanged(state(false, AudioRoute.EARPIECE.route))
        verify(exactly = 1) { listener.onMuteChanged(false) }
        verify(exactly = 1) { listener.onAudioRouteChanged(AudioRoute.EARPIECE) }

        interactor.entryCallAudioStateChanged(state(true, AudioRoute.EARPIECE.route))
        verify(exactly = 1) { listener.onMuteChanged(true) }

        interactor.entryCallAudioStateChanged(state(true, AudioRoute.SPEAKER.route))
        verify(exactly = 1) { listener.onAudioRouteChanged(AudioRoute.SPEAKER) }

        // unchanged values never re-fire: two mute changes, two route changes in total
        verify(exactly = 2) { listener.onMuteChanged(any()) }
        verify(exactly = 2) { listener.onAudioRouteChanged(any()) }
    }
}
