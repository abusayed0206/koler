package com.chooloo.www.chooloolib.ui.settings

import androidx.fragment.app.viewModels
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.interactor.dialog.DialogsInteractor
import com.chooloo.www.chooloolib.ui.base.menu.BaseMenuFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class SettingsFragment @Inject constructor() : BaseMenuFragment() {
    override val viewState: SettingsViewState by viewModels()

    @Inject lateinit var dialogs: DialogsInteractor


    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            askForThemeModeEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let {
                    dialogs.askForThemeMode {
                        viewState.onThemeModeResponse(it)
                        true
                    }
                }
            }

            askForAnimationsEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let { isActivated ->
                    dialogs.askForBoolean(
                        R.string.hint_animations,
                        isActivated
                    ) {
                        viewState.onAnimationsResponse(it)
                        true
                    }
                }
            }
        }
    }
}