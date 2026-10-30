package com.chooloo.www.kontacts.ui.quickcontact

import android.content.ContentUris
import android.content.Intent
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.ui.base.BaseViewState
import com.chooloo.www.chooloolib.util.MutableDataLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuickContactViewState @Inject constructor(

) : BaseViewState() {
    val showContactEvent = MutableDataLiveEvent<Long>()

    fun onIntent(intent: Intent) {
        // exported activity: any app can send a malformed uri here
        val contactId = intent.data?.let { runCatching { ContentUris.parseId(it) }.getOrNull() }
        if (contactId == null || contactId < 0) {
            onError(R.string.error_no_results_contacts)
            onFinish()
        } else {
            showContactEvent.call(contactId)
        }
    }

    fun onFragmentFinish() {
        onFinish()
    }
}
