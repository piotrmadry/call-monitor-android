package com.piotrmadry.callmonitor.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Log(
    @Json(name = "beginning") val beginning: String,
    @Json(name = "duration") val durationInSeconds: String,
    @Json(name = "number") val phoneNumber: String,
    @Json(name = "name") val contactName: String,
    @Json(name = "timesQueried") val timesQueried: Int,
)