package com.chooloo.www.chooloolib.ui.base

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.chooloo.www.chooloolib.databinding.BottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BottomFragment(
    private val fragment: BaseFragment<BaseViewState>? = null
) : BottomSheetDialogFragment(), BaseView<BaseViewState> {
    override val viewState: BaseViewState by viewModels()

    private var _binding: BottomDialogBinding? = null
    private val binding get() = _binding!!

    // not an @AndroidEntryPoint, so an @Inject field here was never injected
    private val baseActivity get() = requireActivity() as BaseActivity<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showsDialog = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(activity, activity?.theme)
        _binding = BottomDialogBinding.inflate(inflater.cloneInContext(contextThemeWrapper))
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetup()
    }

    override fun onSetup() {
        fragment?.setOnFinishListener {
            this@BottomFragment.dismiss()
        }

        viewState.apply {
            attach()

            errorEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let(this@BottomFragment::showError)
            }

            messageEvent.observe(viewLifecycleOwner) {
                it.ifNew?.let(this@BottomFragment::showMessage)
            }
        }

        childFragmentManager.commit {
            fragment?.let { replace(binding.bottomDialogFragmentPlaceholder.id, it) }
        }
    }

    override fun showError(@StringRes stringResId: Int) {
        baseActivity.viewState.onError(stringResId)
    }

    override fun showMessage(@StringRes stringResId: Int) {
        baseActivity.viewState.onMessage(stringResId)
    }
}