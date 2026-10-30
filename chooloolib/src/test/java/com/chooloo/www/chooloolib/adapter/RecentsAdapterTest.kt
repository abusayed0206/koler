package com.chooloo.www.chooloolib.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.test.core.app.ApplicationProvider
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.data.model.RecentAccount
import com.chooloo.www.chooloolib.interactor.drawable.DrawablesInteractor
import com.chooloo.www.chooloolib.interactor.phoneaccounts.PhonesInteractor
import com.chooloo.www.chooloolib.ui.widgets.listitemholder.ListItemHolder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RecentsAdapterTest {
    private val person = ColorDrawable()
    private val phones = mockk<PhonesInteractor> { coEvery { lookupAccount(any()) } returns null }
    private val drawables = mockk<DrawablesInteractor> { every { getDrawable(R.drawable.person) } returns person }
    private val holder = mockk<ListItemHolder>(relaxed = true)
    private val scope = CoroutineScope(Dispatchers.Unconfined)
    private val adapter = RecentsAdapter(
        mockk(relaxed = true), phones, mockk(relaxed = true), drawables, scope, scope,
        ApplicationProvider.getApplicationContext<Context>()
    )

    private fun bind(cachedName: String?) = adapter.onBindListItem(
        holder,
        RecentAccount(Date(), number = "0501234567", type = RecentAccount.TYPE_INCOMING, cachedName = cachedName),
        0
    )

    @Test
    fun unknownCallerGetsPersonDrawableNotInitials() {
        bind(cachedName = "")
        verify { holder.setImageIcon(person) }
        verify(exactly = 0) { holder.imageInitials = any() }
    }

    @Test
    fun cachedNameGetsInitials() {
        bind(cachedName = "John Doe")
        verify { holder.imageInitials = "JD" }
        verify(exactly = 0) { holder.setImageIcon(any()) }
    }
}
