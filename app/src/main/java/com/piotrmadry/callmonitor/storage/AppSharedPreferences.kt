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
            putString(OngoingPhoneNumberKey, phoneNumber)
            apply()
        }
    }

    fun getOngoingCallPhoneNumber(): String? = preferences.getString(OngoingPhoneNumberKey, null)


    fun storeServerStartMs(startDate: Long) {
        with(preferences.edit()) {
            putLong(ServerStartMsKey, startDate)
            apply()
        }
    }

    fun getServerStartMs(): Long = preferences.getLong(ServerStartMsKey, -1)

    companion object {
        const val AppPreferences = "app_preferences"

        const val OngoingPhoneNumberKey = "ongoing_call_phone_number"
        const val ServerStartMsKey = "server_start_ms"
    }
}