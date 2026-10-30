package com.chooloo.www.chooloolib.util

import com.google.common.truth.Truth.assertThat
import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class) // android.net.Uri
@Config(sdk = [33])
class PhoneNumberUtilsTest {
    @Test
    fun numberFromTelUriKeepsLeadingPlus() {
        assertThat(PhoneNumberUtils.numberFromTelUri(Uri.parse("tel:+8801234567890"))).isEqualTo("+8801234567890")
        assertThat(PhoneNumberUtils.numberFromTelUri(Uri.parse("tel:%2B8801234567890"))).isEqualTo("+8801234567890")
        assertThat(PhoneNumberUtils.numberFromTelUri(Uri.parse("tel:*%2306%23"))).isEqualTo("*#06#")
        assertThat(PhoneNumberUtils.numberFromTelUri(null)).isEmpty()
    }

    @Test
    fun uri() {
        assertThat(PhoneNumberUtils.isUri("user@sip.example.com")).isTrue()
        assertThat(PhoneNumberUtils.isUri("user%40sip.example.com")).isTrue()
        assertThat(PhoneNumberUtils.isUri("0501234567")).isFalse()
        assertThat(PhoneNumberUtils.isUri(null)).isFalse()
    }

    @Test
    fun secretCode() {
        assertThat(PhoneNumberUtils.isSecretCode("*#*#4636#*#*")).isTrue()
        assertThat(PhoneNumberUtils.isSecretCode("*#*#1#*#*")).isTrue()
        assertThat(PhoneNumberUtils.isSecretCode("*#*##*#*")).isFalse() // nothing between the markers
        assertThat(PhoneNumberUtils.isSecretCode("*#06#")).isFalse()
        assertThat(PhoneNumberUtils.isSecretCode("*#*#4636")).isFalse()
    }
}
