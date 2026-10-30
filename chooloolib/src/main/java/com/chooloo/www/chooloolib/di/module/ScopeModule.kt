package com.chooloo.www.chooloolib.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Application-wide scopes: they live as long as the app process and are never cancelled,
 * so only use them for work that must outlive any screen. A SupervisorJob keeps one failed
 * child from cancelling the whole scope.
 */
@InstallIn(SingletonComponent::class)
@Module
class ScopeModule {
    @DefaultScope
    @Singleton
    @Provides
    fun provideDefaultScope(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + defaultDispatcher)

    @IoScope
    @Singleton
    @Provides
    fun provideIoScope(@IoDispatcher ioDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + ioDispatcher)

    @MainScope
    @Singleton
    @Provides
    fun provideMainScope(@MainDispatcher mainDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + mainDispatcher)
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultScope

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoScope

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainScope
