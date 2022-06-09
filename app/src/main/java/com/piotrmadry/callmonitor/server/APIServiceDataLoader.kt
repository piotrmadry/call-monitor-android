package com.piotrmadry.callmonitor.server

import com.piotrmadry.callmonitor.response.Log
import com.piotrmadry.callmonitor.response.RootResponse
import com.piotrmadry.callmonitor.response.Service
import com.piotrmadry.callmonitor.response.StatusResponse
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import com.piotrmadry.callmonitor.usecase.CallHistoryUseCase
import com.piotrmadry.callmonitor.utils.NetworkUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APIServiceDataLoader @Inject constructor(
    private val appPreferences: AppSharedPreferences,
    private val callHistory: CallHistoryUseCase,
    private val networkUtils: NetworkUtils
) {

    fun getRootResponse(): RootResponse {
        val localIPAddress = networkUtils.getLocalIPAddressWithPort()

        return RootResponse(
            start = "Date HERE",
            services = listOf(
                Service(
                    name = "status",
                    uri = "${localIPAddress}/status"
                ),
                Service(
                    name = "log",
                    uri = "${localIPAddress}/log"
                )
            )
        )
    }

    fun getLogResponse(): List<Log> {
        return callHistory.getLog()
            .map {
                Log(
                    beginning = it.beginning,
                    durationInSeconds = it.duration,
                    phoneNumber = it.number,
                    contactName = it.name,
                    timesQueried = it.timesQueried
                )
            }
    }

    fun getStatus(): StatusResponse {
        val phoneNumber = appPreferences.getOngoingCallPhoneNumber()
            ?: return StatusResponse(ongoing = false)

        val name = callHistory.getContactNameByPhoneNumber(phoneNumber) ?: "Unknown"

        return StatusResponse(
            ongoing = true,
            contactName = name,
            phoneNumber = phoneNumber
        )
    }
}