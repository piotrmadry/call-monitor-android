package com.piotrmadry.callmonitor.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RootResponse(
    @Json(name = "start") val start: String,
    @Json(name = "services") val services: List<Service>,
)