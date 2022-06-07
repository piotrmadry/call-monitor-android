package com.piotrmadry.callmonitor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSharedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val pref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun storeOngoingCallPhoneNumber(phoneNumber: String?) {
        with (pref.edit()) {
            putString("ongoing_call_phone_number", phoneNumber)
            apply()
        }
    }

    fun getOngoingCallPhoneNumber(): String? = pref.getString("ongoing_call_phone_number", null)
}