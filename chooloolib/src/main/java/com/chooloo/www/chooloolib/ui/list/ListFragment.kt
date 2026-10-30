package com.chooloo.www.chooloolib.ui.list

import android.view.ContextThemeWrapper
import androidx.core.view.isVisible
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.adapter.ListAdapter
import com.chooloo.www.chooloolib.databinding.ItemsBinding
import com.chooloo.www.chooloolib.ui.permissioned.PermissionedFragment

abstract class ListFragment<ItemType, VS : ListViewState<ItemType>> : PermissionedFragment<VS>() {
    override val mainContentView by lazy { binding.root }

    protected open val isFastScrollEnabled get() = true

    protected val binding by lazy {
        ItemsBinding.inflate(
            if (isFastScrollEnabled) layoutInflater.cloneInContext(
                ContextThemeWrapper(layoutInflater.context, R.style.ThemeOverlay_Chooloo_FastScroll)
            ) else layoutInflater
        )
    }


    override fun onSetup() {
        viewState.apply {
            isEmpty.observe(this@ListFragment, this@ListFragment::showEmpty)
            emptyMessage.observe(this@ListFragment, binding.empty.emptyText::setText)
            emptyIcon.observe(this@ListFragment, binding.empty.emptyIcon::setImageResource)

            items.observe(this@ListFragment) {
                adapter.items = it
            }

            filter.observe(this@ListFragment) {
                adapter.titleFilter = it
            }

            isLoading.observe(this@ListFragment) {
                if (it) showEmpty(false)
            }

            onIsObserve(args.getBoolean(ARG_OBSERVE, true))
        }

        adapter.apply {
            setOnItemClickListener(viewState::onItemClick)
            setOnItemLongClickListener(viewState::onItemLongClick)
            setOnItemLeftButtonClickListener(viewState::onItemLeftClick)
            setOnItemRightButtonClickListener(viewState::onItemRightClick)
            binding.itemsScrollView.adapter = this
        }

        args.getString(ARG_FILTER)?.let(viewState::onFilterChanged)
    }

    protected open fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyIcon.isVisible = isShow
            empty.emptyText.isVisible = isShow
            itemsScrollView.isVisible = !isShow
        }
    }


    abstract val adapter: ListAdapter<ItemType>

    companion object {
        const val ARG_FILTER = "filter"
        const val ARG_OBSERVE = "is_observe"
    }
}