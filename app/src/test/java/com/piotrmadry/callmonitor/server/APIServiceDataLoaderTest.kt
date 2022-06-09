package com.piotrmadry.callmonitor.server

import com.piotrmadry.callmonitor.datamodel.LogDataModel
import com.piotrmadry.callmonitor.response.Log
import com.piotrmadry.callmonitor.response.RootResponse
import com.piotrmadry.callmonitor.response.Service
import com.piotrmadry.callmonitor.response.StatusResponse
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import com.piotrmadry.callmonitor.usecase.CallHistoryUseCase
import com.piotrmadry.callmonitor.utils.DateUtils
import com.piotrmadry.callmonitor.utils.NetworkUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class APIServiceDataLoaderTest {

    private val appPreferences = mockk<AppSharedPreferences>()
    private val callHistory = mockk<CallHistoryUseCase>()
    private val networkUtils = mockk<NetworkUtils>()
    private val dateUtils = mockk<DateUtils>()

    @Test
    fun `ensure root response returned correctly`() {
        every { appPreferences.getServerStartMs() } returns System.currentTimeMillis()
        every { dateUtils.toDateTimeWithTimeZone(any(), any()) } returns "2022-06-09T23:41:51+0200"
        every { networkUtils.getLocalIPAddressWithPort() } returns "192.168.68.110:12345"

        val response = create().getRootResponse()

        val expected = RootResponse(
            start = "2022-06-09T23:41:51+0200",
            services = listOf(
                Service(
                    name = "status",
                    uri = "192.168.68.110:12345/status"
                ),
                Service(
                    name = "log",
                    uri = "192.168.68.110:12345/log"
                )
            )
        )

        Assert.assertEquals(response, expected)
    }

    @Test
    fun `ensure log response returned correctly`() {
        every { appPreferences.getLogsTimesQueried() } returns mutableMapOf()
        every { appPreferences.storeLogsTimesQueried(any()) } returns Unit
        every { dateUtils.toDateTimeWithTimeZone(any(), any()) } returns "2022-06-09T23:41:51+0200"

        every { callHistory.getLog() } returns listOf(
            LogDataModel(
                id = "id",
                beginning = -1,
                duration = "1",
                number = "+48111111111",
                name = "Joe Doe"
            )
        )


        val response = create().getLogResponse()

        val expected = listOf(
            Log(
                beginning = "2022-06-09T23:41:51+0200",
                durationInSeconds = "1",
                phoneNumber = "+48111111111",
                contactName = "Joe Doe",
                timesQueried = 1
            )
        )

        Assert.assertEquals(response, expected)
    }

    @Test
    fun `ensure status response returned correctly - for ongoing call`() {
        every { appPreferences.getOngoingCallPhoneNumber() } returns "+48111111111"
        every { callHistory.getContactNameByPhoneNumber(any()) } returns "Joe Doe"

        val response = create().getStatusResponse()

        val expected = StatusResponse(
            ongoing = true,
            phoneNumber = "+48111111111",
            contactName = "Joe Doe"
        )

        Assert.assertEquals(response, expected)
    }

    @Test
    fun `ensure status response returned correctly - for no call`() {
        every { appPreferences.getOngoingCallPhoneNumber() } returns null

        val response = create().getStatusResponse()

        val expected = StatusResponse(
            ongoing = false
        )

        Assert.assertEquals(response, expected)
    }

    private fun create(): APIServiceDataLoader = APIServiceDataLoader(
        appPreferences = appPreferences,
        callHistory = callHistory,
        networkUtils = networkUtils,
        dateUtils = dateUtils
    )
}