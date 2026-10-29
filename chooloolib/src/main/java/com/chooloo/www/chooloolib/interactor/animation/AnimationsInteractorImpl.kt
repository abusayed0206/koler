package com.chooloo.www.chooloolib.interactor.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.interactor.preferences.PreferencesInteractor
import com.chooloo.www.chooloolib.util.baseobservable.BaseObservable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimationsInteractorImpl @Inject constructor(
    private val preferencesInteractor: PreferencesInteractor
) :
    BaseObservable<AnimationsInteractor.Listener>(),
    AnimationsInteractor {

    private val _isEnabled: Boolean
        get() = preferencesInteractor.isAnimations


    override fun focus(view: View) {
        if (_isEnabled) {
            ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
                duration = 800
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
            }.start()
        }
    }

    override fun show(view: View, ifGone: Boolean) {
        if (view.isVisible && ifGone) {
            return
        }
        view.visibility = VISIBLE
        if (_isEnabled) {
            view.alpha = 0f
            view.translationY = -view.height.toFloat()
            view.animate().alpha(1f).translationY(0f).setDuration(100)
        }
    }

    override fun blink(view: View, totalDuration: Long, duration: Long) {
        if (_isEnabled) {
            view.startAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                setDuration(duration)
                startOffset = 20
                repeatMode = Animation.REVERSE
                repeatCount = Animation.INFINITE
            })
            Handler(Looper.getMainLooper()).postDelayed({ view.clearAnimation() }, totalDuration)
        }
    }

    override fun hide(view: View, ifVisible: Boolean, goneOrInvisible: Boolean) {
        if (view.visibility != VISIBLE && ifVisible) {
            return
        }
        if (_isEnabled) {
            view.animate().alpha(0f).translationY(view.height / 4f).setDuration(250).withEndAction {
                view.visibility = if (goneOrInvisible) View.GONE else View.INVISIBLE
                view.alpha = 1f
                view.translationY = 0f
            }
        } else {
            view.visibility = if (goneOrInvisible) View.GONE else View.INVISIBLE
        }
    }


    override fun animateRecyclerView(recyclerView: RecyclerView) {
        if (_isEnabled) {
            recyclerView.apply {
                layoutAnimation = AnimationUtils.loadLayoutAnimation(
                    recyclerView.context,
                    R.anim.layout_animation_fade_in
                )
                scheduleLayoutAnimation()
            }
        }
    }
}