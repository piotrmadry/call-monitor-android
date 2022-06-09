package com.piotrmadry.callmonitor.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @Json(name = "ongoing") val ongoing: Boolean,
    @Json(name = "number") val phoneNumber: String? = null,
    @Json(name = "name") val contactName: String? = null,
)