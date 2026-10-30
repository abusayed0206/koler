package com.chooloo.www.chooloolib.interactor.audio

import android.media.AudioManager
import android.media.ToneGenerator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AudiosInteractorImplTest {
    private val audioManager = mockk<AudioManager>(relaxed = true) {
        every { ringerMode } returns AudioManager.RINGER_MODE_NORMAL
    }
    private val audios = AudiosInteractorImpl(mockk(relaxed = true), audioManager)

    private fun generator() = AudiosInteractorImpl::class.java.getDeclaredField("toneGenerator")
        .apply { isAccessible = true }.get(audios)

    @Test
    fun manyKeyPressesReuseOneToneGenerator() {
        audios.playToneByChar('1')
        val first = generator()
        assertThat(first).isInstanceOf(ToneGenerator::class.java)
        repeat(500) { audios.playToneByChar('5') }
        assertThat(generator()).isSameInstanceAs(first)
    }

    @Test
    fun silentModePlaysNothing() {
        every { audioManager.ringerMode } returns AudioManager.RINGER_MODE_SILENT
        audios.playToneByChar('1')
        assertThat(generator()).isNull()
    }
}
