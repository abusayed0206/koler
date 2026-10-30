package com.chooloo.www.koler.ui.settings

import androidx.fragment.app.activityViewModels
import com.chooloo.www.chooloolib.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.chooloo.www.chooloolib.ui.settings.SettingsFragment as ChoolooSettingsFragment

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : ChoolooSettingsFragment() {
    override val viewState: SettingsViewState by activityViewModels()

    override fun onSetup() {
        super.onSetup()

        viewState.apply {
            askForDefaultPageEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let {
                    dialogs.askForDefaultPage {
                        viewState.onDefaultPageResponse(it)
                        true
                    }
                }
            }

            askForDialpadTonesEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let { isActivated ->
                    dialogs.askForBoolean(
                        R.string.hint_dialpad_tones,
                        isActivated
                    ) {
                        viewState.onDialpadTones(it)
                        true
                    }
                }
            }

            askForDialpadVibrateEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let { isActivated ->
                    dialogs.askForBoolean(
                        R.string.hint_dialpad_vibrate,
                        isActivated
                    ) {
                        viewState.onDialpadVibrate(it)
                        true
                    }
                }
            }

            askForGroupRecentsEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let { isActivated ->
                    dialogs.askForBoolean(
                        R.string.hint_group_recents,
                        isActivated
                    ) {
                        viewState.onGroupRecents(it)
                        true
                    }
                }
            }

            clearRecentsEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let {
                    dialogs.askForValidation(R.string.explain_clear_recents) { bl ->
                        if (bl) viewState.onClearRecents()
                    }
                }
            }

            askForIncomingCallModeEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let {
                    dialogs.askForIncomingCallMode {
                        viewState.onIncomingCallMode(it)
                        true
                    }
                }
            }
        }
    }
}