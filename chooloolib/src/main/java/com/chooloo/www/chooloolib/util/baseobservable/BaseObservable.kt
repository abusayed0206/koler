package com.chooloo.www.chooloolib.util.baseobservable

import java.util.*
import kotlin.collections.HashSet

open class BaseObservable<Listener> : IBaseObservable<Listener> {
    private val _listeners by lazy { HashSet<Listener>() }

    val listeners: Set<Listener>
        get() = Collections.unmodifiableSet(HashSet(_listeners))

    @Synchronized
    override fun registerListener(listener: Listener) {
        if (_listeners.add(listener) && _listeners.size == 1) {
            onStartedObserved()
        }
    }

    @Synchronized
    override fun unregisterListener(listener: Listener) {
        if (_listeners.remove(listener) && _listeners.isEmpty()) {
            onFinishedObserved()
        }
    }

    override fun invokeListeners(invoker: (Listener) -> Unit) {
        listeners.forEach(invoker::invoke)
    }

    protected open fun onStartedObserved() {}
    protected open fun onFinishedObserved() {}
}