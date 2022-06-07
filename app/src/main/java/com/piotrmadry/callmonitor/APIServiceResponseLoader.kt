package com.piotrmadry.callmonitor

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APIServiceResponseLoader @Inject constructor(
    private val callHistory: CallHistory,
    private val networkHelper: NetworkHelper
) {

    fun getRootResponse(): RootResponse {
        val localIPAddress = networkHelper.getLocalIPAddressWithPort()
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
}