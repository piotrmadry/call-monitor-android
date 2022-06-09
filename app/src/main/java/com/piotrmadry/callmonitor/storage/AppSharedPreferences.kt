package com.piotrmadry.callmonitor.storage

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSharedPreferences @Inject constructor(
    @ApplicationContext context: Context,
    private val moshi: Moshi
) {
    private val preferences = context.getSharedPreferences(AppPreferences, Context.MODE_PRIVATE)
    private val mapType: ParameterizedType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        Int::class.javaObjectType
    )

    init {
        preferences.edit().clear().apply()
    }

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

    fun storeLogsTimesQueried(map: Map<String, Int>) {
        val timesQueried = moshi.adapter<Map<String, Int>>(mapType).toJson(map).toString()

        with(preferences.edit()) {
            putString(LogsTimeQueriedMsKey, timesQueried)
            apply()
        }
    }

    fun getLogsTimesQueried(): MutableMap<String, Int> {
        val timesQueriedString =
            preferences.getString(LogsTimeQueriedMsKey, null) ?: return mutableMapOf()


        return moshi.adapter<Map<String, Int>>(mapType).fromJson(timesQueriedString)?.toMutableMap()
            ?: mutableMapOf()
    }

    companion object {
        const val AppPreferences = "app_preferences"

        const val OngoingPhoneNumberKey = "ongoing_call_phone_number"
        const val ServerStartMsKey = "server_start_ms"
        const val LogsTimeQueriedMsKey = "logs_time_queried"
    }
}