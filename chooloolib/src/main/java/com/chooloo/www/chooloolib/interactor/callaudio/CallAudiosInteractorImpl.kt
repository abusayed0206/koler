package com.chooloo.www.chooloolib.interactor.callaudio

import android.telecom.CallAudioState
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor.AudioRoute
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor.AudioRoute.*
import com.chooloo.www.chooloolib.api.service.CallService
import com.chooloo.www.chooloolib.util.baseobservable.BaseObservable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CallAudiosInteractorImpl @Inject constructor() :
    BaseObservable<CallAudiosInteractor.Listener>(),
    CallAudiosInteractor {

    private val callAudioState get() = CallService.sInstance?.callAudioState


    override var isMuted: Boolean?
        get() = callAudioState?.isMuted
        set(value) {
            value?.let { CallService.sInstance?.setMuted(it) }
        }

    override var isSpeakerOn: Boolean?
        get() = audioRoute == SPEAKER
        set(value) {
            if (value == true) {
                audioRoute = SPEAKER
            } else if (value == false && audioRoute != BLUETOOTH) {
                audioRoute = WIRED_OR_EARPIECE
            }
        }

    override var audioRoute: AudioRoute?
        get() = Companion.fromRoute(callAudioState?.route)
        set(value) {
            value?.route?.let { CallService.sInstance?.setAudioRoute(it) }
        }

    override val supportedAudioRoutes: Array<AudioRoute>
        get() = values().filter {
            callAudioState?.supportedRouteMask?.let(it.route::and) == it.route
        }.toTypedArray()


    private var _lastAudioState: CallAudioState? = null

    override fun entryCallAudioStateChanged(audioState: CallAudioState) {
        val last = _lastAudioState
        _lastAudioState = audioState
        if (last == null || last.isMuted != audioState.isMuted) {
            invokeListeners { it.onMuteChanged(audioState.isMuted) }
        }
        if (last == null || last.route != audioState.route) {
            AudioRoute.fromRoute(audioState.route)?.let { route ->
                invokeListeners { it.onAudioRouteChanged(route) }
            }
        }
    }
}