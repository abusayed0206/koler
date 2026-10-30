package com.chooloo.www.chooloolib.data.repository.calls

import com.chooloo.www.chooloolib.data.model.Call
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Holds the calls for the process lifetime, independent of CallService; the service writes to it. */
@Singleton
class CallsRepositoryImpl @Inject constructor() : CallsRepository {
    private val _calls = MutableStateFlow<List<Call>>(emptyList())

    override fun getCalls(): Flow<List<Call>> = _calls.asStateFlow()

    fun addCall(call: Call) = _calls.update { it + call }

    fun removeCall(call: Call) = _calls.update { it - call }
}
