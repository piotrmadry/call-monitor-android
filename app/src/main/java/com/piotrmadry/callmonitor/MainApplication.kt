package com.piotrmadry.callmonitor

import android.app.Application
import android.content.IntentFilter
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var receiver: IncomingCallReceiver

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, IntentFilter("android.intent.action.PHONE_STATE"))
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)
    }
}