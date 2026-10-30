package com.chooloo.www.chooloolib.util

import android.content.ComponentName
import android.net.Uri
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PhoneAccountUtilsTest {
    private fun account(address: Uri?) =
        PhoneAccount.builder(PhoneAccountHandle(ComponentName("pkg", "cls"), "id"), "SIM 1")
            .setAddress(address).build()

    @Test
    fun telAddressIsDecodedNotPercentEncoded() {
        val sim = account(Uri.fromParts("tel", "+8801234567890", null)) // stored as tel:%2B880...
        assertThat(sim.fullAddress()).isEqualTo("+8801234567890")
        assertThat(sim.fullLabel()).isEqualTo("SIM 1(+8801234567890)")
    }

    @Test
    fun emptyTelAddressShowsLabelOnly() {
        val sim = account(Uri.parse("tel:"))
        assertThat(sim.fullLabel()).isEqualTo("SIM 1")
        assertThat(sim.fullAddress()).isEqualTo("tel:")
    }
}
