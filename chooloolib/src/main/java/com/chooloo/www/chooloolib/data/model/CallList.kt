package com.chooloo.www.chooloolib.data.model

import java.util.*

class CallList {
    private var _callsById: HashMap<String, Call> = HashMap()

    // all reads go through synchronized accessors; the rest filter the [calls] snapshot
    val size: Int
        @Synchronized get() = _callsById.size

    val calls: List<Call>
        @Synchronized get() = ArrayList(_callsById.values)

    val conferenceCalls
        get() = calls.filter(Call::isConference)

    val nonConferenceCalls
        get() = calls.filter { call -> !call.isInConference && !call.isConference }

    val conferenceableCalls
        get() = mutableListOf<Call>().apply {
            addAll(calls.filter(Call::isConferenceParent))
            addAll(calls.filter(Call::isHolding))
            addAll(calls.filter(Call::isActive))
        }.distinct()

    val conferenceParentCalls
        get() = calls.filter(Call::isConferenceParent)


    @Synchronized
    operator fun get(callId: String) =
        _callsById[callId]

    @Synchronized
    operator fun contains(call: Call) =
        _callsById.containsKey(call.id)


    fun getIndex(index: Int): Call? =
        calls.getOrNull(index)

    fun hasState(state: Call.State) =
        calls.any { call -> call.state == state }

    fun getState(state: Call.State) =
        calls.filter { call -> call.state == state }

    fun getFirstState(state: Call.State) =
        calls.firstOrNull { call -> call.state == state }

    fun hasStates(states: Array<Call.State>) =
        states.all(this::hasState)

    fun getNotStates(states: Array<Call.State>) =
        calls.filter { call -> states.none { state -> state == call.state } }

    fun getByTelecomCall(telecomCall: android.telecom.Call) =
        calls.firstOrNull { call -> call.telecomCall == telecomCall }


    @Synchronized
    fun update(call: Call) {
        _callsById[call.id] = call
    }

    /**
     * Try to remove a given call from hash map values
     *
     *
     * If no call exist with the same id, try to remove by telecom call
     * In case of onCallRemoved entry, a call that's already exists will be created with a new id
     *
     *
     * @param call Koler call item to remove
     */
    @Synchronized
    fun remove(call: Call) {
        if (_callsById.containsKey(call.id)) {
            _callsById.remove(call.id)
        } else {
            calls.filter { c -> c == call }.forEach(_callsById.values::remove)
        }
    }
}