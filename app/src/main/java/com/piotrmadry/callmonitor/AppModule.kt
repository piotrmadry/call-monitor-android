package com.piotrmadry.callmonitor

import android.app.Application
import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideContentResolver(application: Application): ContentResolver =
        application.contentResolver

    @Provides
    @IO
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IO