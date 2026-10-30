package com.chooloo.www.chooloolib.interactor.proximity

import android.annotation.SuppressLint
import android.os.PowerManager
import android.os.PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY
import android.os.PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK
import com.chooloo.www.chooloolib.util.baseobservable.BaseObservable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProximitiesInteractorImpl @Inject constructor(
    private val powerManager: PowerManager
) : BaseObservable<ProximitiesInteractor.Listener>(),
    ProximitiesInteractor {

    private val _wakeLock by lazy {
        powerManager.newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "koler:screen_wake_lock")
    }

    // no timeout: held for the whole call screen, released in CallViewState.detach()
    @SuppressLint("WakelockTimeout")
    override fun acquire() {
        if (!_wakeLock.isHeld) {
            _wakeLock.acquire()
        }
    }

    override fun release() {
        if (_wakeLock.isHeld) {
            _wakeLock.release(RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY)
        }
    }
}