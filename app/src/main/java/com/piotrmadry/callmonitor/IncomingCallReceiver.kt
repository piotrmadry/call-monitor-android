package com.piotrmadry.callmonitor

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import javax.inject.Inject

class IncomingCallReceiver @Inject constructor(private val pref: AppSharedPreferences) :
    BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.extras?.getString(TelephonyManager.EXTRA_STATE)) {
            TelephonyManager.EXTRA_STATE_RINGING -> pref.storeOngoingCallPhoneNumber(
                intent.extras?.getString(
                    TelephonyManager.EXTRA_INCOMING_NUMBER
                )
            )
            else -> pref.storeOngoingCallPhoneNumber(null)
        }
    }
}