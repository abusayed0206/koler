package com.chooloo.www.chooloolib.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.chooloo.www.chooloolib.di.factory.fragment.FragmentFactory
import com.chooloo.www.chooloolib.interactor.permission.PermissionsInteractor
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseFragment<out VM : BaseViewState> : Fragment(), BaseView<VM> {
    private var _onFinishListener: () -> Unit = {}
    private var _hasView = false
    private val _viewLazies = mutableListOf<ViewLazy<*>>()

    val args get() = arguments ?: Bundle()

    @Inject lateinit var baseActivity: BaseActivity<*>
    @Inject lateinit var fragmentFactory: FragmentFactory
    @Inject lateinit var permissions: PermissionsInteractor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _hasView = true
        return contentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewState.apply {
            errorEvent.observe(this@BaseFragment) {
                it.ifNew?.let(this@BaseFragment::showError)
            }

            finishEvent.observe(this@BaseFragment) {
                it.ifNew?.let { finish() }
            }

            messageEvent.observe(this@BaseFragment) {
                it.ifNew?.let(this@BaseFragment::showMessage)
            }
        }

        baseActivity.onFragmentSetup(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetupHook()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _hasView = false
        _viewLazies.forEach(ViewLazy<*>::clear)
    }

    override fun showError(@StringRes stringResId: Int) {
        baseActivity.viewState.onError(stringResId)
    }

    override fun showMessage(@StringRes stringResId: Int) {
        baseActivity.viewState.onMessage(stringResId)
    }

    override fun finish() {
        super.finish()
        _onFinishListener.invoke()
    }

    /** Called from onViewCreated, once per view - observe with viewLifecycleOwner. */
    protected open fun onSetupHook() {
        onSetup()
        viewState.attach()
    }

    fun setOnFinishListener(onFinishListener: () -> Unit) {
        _onFinishListener = onFinishListener
    }

    /**
     * Lazy value tied to the fragment's view (bindings, views): created on first access while
     * the view exists and dropped in onDestroyView, so back-stack re-entry inflates a fresh view.
     */
    protected fun <T : Any> viewLazy(initializer: () -> T): ReadOnlyProperty<Any?, T> =
        ViewLazy({ _hasView }, initializer).also(_viewLazies::add)

    private class ViewLazy<T : Any>(
        private val hasView: () -> Boolean,
        private val initializer: () -> T
    ) : ReadOnlyProperty<Any?, T> {
        private var _value: T? = null

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            check(hasView()) { "${property.name} accessed outside the fragment view lifecycle" }
            return _value ?: initializer().also { _value = it }
        }

        fun clear() {
            _value = null
        }
    }


    abstract val contentView: View
    abstract override val viewState: VM
}
