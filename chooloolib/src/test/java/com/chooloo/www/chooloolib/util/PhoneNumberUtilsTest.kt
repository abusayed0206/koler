package com.chooloo.www.chooloolib.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PhoneNumberUtilsTest {
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
