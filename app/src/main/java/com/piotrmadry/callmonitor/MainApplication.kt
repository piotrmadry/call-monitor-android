package com.piotrmadry.callmonitor

import android.app.Application
import android.content.IntentFilter
import com.piotrmadry.callmonitor.broadcast.IncomingCallReceiver
import com.piotrmadry.callmonitor.server.APIServiceImpl
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import com.piotrmadry.callmonitor.utils.IntentUtils
import com.piotrmadry.httpserver.HttpServer
import com.piotrmadry.httpserver.Server
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    private lateinit var server: HttpServer

    @Inject
    lateinit var apiServiceImpl: APIServiceImpl

    @Inject
    lateinit var appPreferences: AppSharedPreferences

    @Inject
    lateinit var receiver: IncomingCallReceiver

    override fun onCreate() {
        super.onCreate()
        registerReceiver(
            receiver,
            IntentFilter(IntentUtils.ACTION_PHONE_STATE)
        )

        server = HttpServer(
            port = Server.Port,
            service = apiServiceImpl
        ).apply {
            start()
            appPreferences.storeServerStartMs(System.currentTimeMillis())
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)
        server.stop()
    }
}