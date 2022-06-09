package com.piotrmadry.callmonitor.server

import com.piotrmadry.callmonitor.response.Log
import com.piotrmadry.callmonitor.response.RootResponse
import com.piotrmadry.callmonitor.response.Service
import com.piotrmadry.callmonitor.response.StatusResponse
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class APIServiceImplTest {

    private val dataLoader = mockk<APIServiceDataLoader>()

    @Test
    fun `ensure return empty json list for empty log`() {
        every { dataLoader.getLogResponse() } returns listOf()
        val log = create().getLog()
        Assert.assertEquals(log, "[]")
    }

    @Test
    fun `ensure return non empty json for log`() {
        every { dataLoader.getLogResponse() } returns listOf(
            Log(
                beginning = "2022-06-09T23:41:51+0200",
                durationInSeconds = "0",
                phoneNumber = "+48609636122",
                contactName = "Mom",
                timesQueried = 1
            )
        )
        val log = create().getLog()

        val expected =
            "[{\"beginning\":\"2022-06-09T23:41:51+0200\",\"duration\":\"0\",\"number\":\"+48609636122\",\"name\":\"Mom\",\"timesQueried\":1}]"

        Assert.assertEquals(log, expected)
    }

    @Test
    fun `ensure return root json correctly `() {
        every { dataLoader.getRootResponse() } returns RootResponse(
            start = "2022-06-09T23:16:44+0200",
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
        val root = create().getRoot()

        val expected =
            "{\"start\":\"2022-06-09T23:16:44+0200\",\"services\":[{\"name\":\"status\",\"uri\":\"192.168.68.110:12345/status\"},{\"name\":\"log\",\"uri\":\"192.168.68.110:12345/log\"}]}"

        Assert.assertEquals(root, expected)
    }

    @Test
    fun `ensure return status json correctly - for no call`() {
        every { dataLoader.getStatusResponse() } returns StatusResponse(
            ongoing = false
        )
        val status = create().getStatus()

        val expected = "{\"ongoing\":false}"

        Assert.assertEquals(status, expected)
    }

    @Test
    fun `ensure return status json correctly - for ongoing call`() {
        every { dataLoader.getStatusResponse() } returns StatusResponse(
            ongoing = true,
            contactName = "Mom",
            phoneNumber = "+48111111111"
        )
        val status = create().getStatus()

        val expected = "{\"ongoing\":true,\"number\":\"+48111111111\",\"name\":\"Mom\"}"

        Assert.assertEquals(status, expected)
    }


    private fun create(): APIServiceImpl = APIServiceImpl(
        moshi = Moshi.Builder().build(),
        dataLoader = dataLoader
    )
}