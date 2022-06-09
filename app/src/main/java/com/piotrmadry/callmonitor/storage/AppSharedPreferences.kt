package com.piotrmadry.callmonitor.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSharedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(AppPreferences, Context.MODE_PRIVATE)

    fun storeOngoingCallPhoneNumber(phoneNumber: String?) {
        with(preferences.edit()) {
            putString(OngoingPhoneNumber, phoneNumber)
            apply()
        }
    }

    fun getOngoingCallPhoneNumber(): String? = preferences.getString(OngoingPhoneNumber, null)

    companion object {
        const val AppPreferences = "app_preferences"
        const val OngoingPhoneNumber = "ongoing_call_phone_number"
    }
}