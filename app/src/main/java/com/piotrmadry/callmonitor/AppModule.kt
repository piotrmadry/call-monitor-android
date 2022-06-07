package com.piotrmadry.callmonitor

import android.app.Application
import android.content.ContentResolver
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @App
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Singleton
    @Provides
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @IO
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IO

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class App