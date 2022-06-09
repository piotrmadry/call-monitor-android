package com.piotrmadry.callmonitor.di.module

import android.app.Application
import android.content.ContentResolver
import com.piotrmadry.callmonitor.di.qualifier.IO
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

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

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}


