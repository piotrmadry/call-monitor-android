package com.piotrmadry.callmonitor.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import javax.inject.Inject

class IncomingCallReceiver @Inject constructor(
    private val appPreferences: AppSharedPreferences
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.extras?.getString(TelephonyManager.EXTRA_STATE)) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val incomingPhoneNumber =
                    intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                appPreferences.storeOngoingCallPhoneNumber(incomingPhoneNumber)
            }
            else -> appPreferences.storeOngoingCallPhoneNumber(null)
        }
    }
}