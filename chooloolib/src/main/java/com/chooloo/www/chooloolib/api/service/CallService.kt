package com.chooloo.www.chooloolib.api.service

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.telecom.CallAudioState
import android.telecom.InCallService
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor
import com.chooloo.www.chooloolib.interactor.calls.CallsInteractor
import com.chooloo.www.chooloolib.interactor.preferences.PreferencesInteractor
import com.chooloo.www.chooloolib.interactor.preferences.PreferencesInteractor.Companion.IncomingCallMode
import com.chooloo.www.chooloolib.data.model.Call
import com.chooloo.www.chooloolib.notification.CallNotification
import com.chooloo.www.chooloolib.data.repository.calls.CallsRepositoryImpl
import com.chooloo.www.chooloolib.ui.call.CallActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("NewApi")
@AndroidEntryPoint
class CallService : InCallService() {
    @Inject lateinit var callAudios: CallAudiosInteractor
    // concrete type: same @Singleton as the bound CallsRepository, plus its write side
    @Inject lateinit var callsRepository: CallsRepositoryImpl
    @Inject lateinit var callsInteractor: CallsInteractor
    @Inject lateinit var preferences: PreferencesInteractor
    @Inject lateinit var callNotification: CallNotification


    override fun onCreate() {
        super.onCreate()
        sInstance = this
        callNotification.attach()
    }

    override fun onDestroy() {
        callNotification.detach()
        if (sInstance === this) sInstance = null
        super.onDestroy()
    }

    override fun onCallAdded(telecomCall: android.telecom.Call) {
        super.onCallAdded(telecomCall)
        val call = Call.from(telecomCall)
        callsRepository.addCall(call)
        callsInteractor.entryAddCall(call)
        if (!sIsActivityActive && (preferences.incomingCallMode == IncomingCallMode.FULL_SCREEN || call.isDirectionOutgoing)) {
            startCallActivity()
        }
    }


    override fun onCallRemoved(telecomCall: android.telecom.Call) {
        super.onCallRemoved(telecomCall)
        val call = callsInteractor.getCallByTelecomCall(telecomCall) ?: Call.from(telecomCall)
        callsRepository.removeCall(call)
        callsInteractor.entryRemoveCall(call)
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        super.onCallAudioStateChanged(audioState)
        callAudios.entryCallAudioStateChanged(audioState)
    }

    private fun startCallActivity() {
        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    companion object {
        var sIsActivityActive = false
        var sInstance: CallService? = null
    }
}
