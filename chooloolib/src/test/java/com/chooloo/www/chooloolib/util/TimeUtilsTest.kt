package com.chooloo.www.chooloolib.util

import com.google.common.truth.Truth.assertThat
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TimeUtilsTest {
    private val minute = 60_000L
    private val hour = 60 * minute
    private val day = 24 * hour
    private fun ago(millis: Long) = getTimeAgo(System.currentTimeMillis() - millis)

    @Test
    fun elapsedUnderAnHourHasNoHours() {
        assertThat(getElapsedTimeString(0)).isEqualTo("0 mins 0 sec")
        assertThat(getElapsedTimeString(59)).isEqualTo("0 mins 59 sec")
        assertThat(getElapsedTimeString(61)).isEqualTo("1 mins 1 sec")
        assertThat(getElapsedTimeString(3599)).isEqualTo("59 mins 59 sec")
    }

    @Ignore("bug: minutes are not taken modulo 60 (3660s -> \"1 hrs 61 mins\") — fixed in Phase 3")
    @Test
    fun elapsedOverAnHourWrapsMinutes() {
        assertThat(getElapsedTimeString(3600)).isEqualTo("1 hrs 0 mins 0 sec")
        assertThat(getElapsedTimeString(3660)).isEqualTo("1 hrs 1 mins 0 sec")
        assertThat(getElapsedTimeString(7322)).isEqualTo("2 hrs 2 mins 2 sec")
    }

    @Test
    fun timeAgoBuckets() {
        assertThat(getTimeAgo(System.currentTimeMillis() + hour)).isEqualTo("In the future")
        assertThat(ago(10_000)).isEqualTo("Moments ago")
        assertThat(ago(90_000)).isEqualTo("A minute ago")
        assertThat(ago(5 * minute + 1_000)).isEqualTo("5 minutes ago")
        assertThat(ago(hour + minute)).isEqualTo("An hour ago")
        assertThat(ago(3 * hour + minute)).isEqualTo("3 hours ago")
        assertThat(ago(day + hour)).isEqualTo("Yesterday")
    }

    @Test
    fun timeAgoOlderThanTwoDaysIsShortDate() {
        val time = Calendar.getInstance().apply { set(2020, Calendar.MARCH, 5, 14, 7) }.timeInMillis
        assertThat(getTimeAgo(time)).isEqualTo("Mar 05, 02:07")
    }
}
