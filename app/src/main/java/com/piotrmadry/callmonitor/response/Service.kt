package com.piotrmadry.callmonitor.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Service(
    @Json(name = "name") val name: String,
    @Json(name = "uri") val uri: String,
)