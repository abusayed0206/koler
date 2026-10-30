package com.chooloo.www.chooloolib.util.baseobservable

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BaseObservableTest {
    private class Counting : BaseObservable<String>() {
        var started = 0
        var finished = 0
        override fun onStartedObserved() { started++ }
        override fun onFinishedObserved() { finished++ }
    }

    private val observable = Counting()

    @Test
    fun firstRegisterStartsAndLastUnregisterFinishes() {
        observable.registerListener("a")
        observable.registerListener("b")
        assertThat(observable.started).isEqualTo(1)
        observable.unregisterListener("a")
        assertThat(observable.finished).isEqualTo(0)
        observable.unregisterListener("b")
        assertThat(observable.finished).isEqualTo(1)
    }

    @Test
    fun registeringTwiceKeepsOneListener() {
        observable.registerListener("a")
        observable.registerListener("a")
        assertThat(observable.listeners).containsExactly("a")
    }

    @Test
    fun invokeListenersCallsEachListener() {
        observable.registerListener("a")
        observable.registerListener("b")
        val called = mutableListOf<String>()
        observable.invokeListeners { called += it }
        assertThat(called).containsExactly("a", "b")
    }

    @Test
    fun listenerCanUnregisterWhileInvoked() {
        observable.registerListener("a")
        observable.registerListener("b")
        observable.invokeListeners(observable::unregisterListener) // iterates a snapshot, no CME
        assertThat(observable.listeners).isEmpty()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun listenersIsReadOnly() {
        @Suppress("UNCHECKED_CAST")
        (observable.listeners as MutableSet<String>).add("x")
    }

    @Test
    fun reRegisteringSoleListenerDoesNotRestart() {
        observable.registerListener("a")
        observable.registerListener("a")
        assertThat(observable.started).isEqualTo(1)
    }

    @Test
    fun unregisteringUnknownListenerDoesNotFinish() {
        observable.unregisterListener("never-registered")
        assertThat(observable.finished).isEqualTo(0)
    }
}
