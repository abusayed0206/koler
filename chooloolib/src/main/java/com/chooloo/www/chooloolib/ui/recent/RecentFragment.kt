package com.chooloo.www.chooloolib.ui.recent

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil3.load
import com.chooloo.www.chooloolib.databinding.RecentBinding
import com.chooloo.www.chooloolib.interactor.prompt.PromptsInteractor
import com.chooloo.www.chooloolib.interactor.telecom.TelecomInteractor
import com.chooloo.www.chooloolib.ui.base.BaseFragment
import com.chooloo.www.chooloolib.ui.recent.menu.RecentMenuViewState
import com.chooloo.www.chooloolib.ui.recents.RecentsViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecentFragment @Inject constructor() : BaseFragment<RecentViewState>() {
    override val contentView by viewLazy { binding.root }
    override val viewState: RecentViewState by viewModels()

    private val historyViewState: RecentsViewState by viewModels()
    private val menuViewState: RecentMenuViewState by activityViewModels()
    private val binding by viewLazy { RecentBinding.inflate(layoutInflater) }

    @Inject lateinit var prompts: PromptsInteractor
    @Inject lateinit var telecomInteractor: TelecomInteractor


    override fun onSetup() {
        binding.recentContactImage.isVisible = false

        binding.recentMainActions.apply {
            recentButtonSms.setOnClickListener { viewState.onSms() }
            recentButtonCall.setOnClickListener { viewState.onCall() }
            recentButtonMore.setOnClickListener { viewState.onMoreClick() }
            recentButtonContact.setOnClickListener { viewState.onOpenContact() }
            recentButtonAddContact.setOnClickListener { viewState.onAddContact() }
        }

        viewState.apply {
            onRecentId(args.getLong(ARG_RECENT_ID))
            isBlocked.observe(viewLifecycleOwner, menuViewState::onIsBlocked)
            recentNumber.observe(viewLifecycleOwner, menuViewState::onRecentNumber)
            typeImage.observe(viewLifecycleOwner, binding.recentTypeImage::setImageResource)

            imageUri.observe(viewLifecycleOwner) {
                binding.recentContactImage.isVisible = true
                binding.recentContactImage.load(it)
            }

            name.observe(viewLifecycleOwner) {
                binding.recentTextName.text = it
            }

            caption.observe(viewLifecycleOwner) {
                binding.recentCaption.text = it
            }

            isContactVisible.observe(viewLifecycleOwner) {
                binding.recentMainActions.recentButtonContact.isVisible = it
            }

            isAddContactVisible.observe(viewLifecycleOwner) {
                binding.recentMainActions.recentButtonAddContact.isVisible = it
            }

            callEvent.observe(viewLifecycleOwner) { ev ->
                ev.ifNew?.let(telecomInteractor::callNumber)
            }

            showMoreEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let {
                    prompts.showFragment(fragmentFactory.getRecentMenuFragment())
                }
            }

            showContactEvent.observe(viewLifecycleOwner) { ev ->
                ev.ifNew?.let {
                    prompts.showFragment(fragmentFactory.getBriefContactFragment(it))
                }
            }

            showRecentEvent.observe(viewLifecycleOwner) { ev ->
                ev.ifNew?.let { prompts.showFragment(fragmentFactory.getRecentFragment(it)) }
            }
        }

        historyViewState.itemClickedEvent.observe(viewLifecycleOwner) {
            it.ifNew?.let { recent -> viewState.onRecentClick(recent.id) }
        }

        menuViewState.apply {
            finishEvent.observe(viewLifecycleOwner) {
                viewState.onFinish()
            }

            onRecentId(args.getLong(ARG_RECENT_ID))
        }
    }

    companion object {
        const val ARG_RECENT_ID = "recent_id"

        fun newInstance(recentId: Long) = RecentFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_RECENT_ID, recentId)
            }
        }
    }
}