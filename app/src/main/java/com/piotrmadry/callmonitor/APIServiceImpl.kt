package com.piotrmadry.callmonitor

import com.piotrmadry.httpserver.APIService
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import javax.inject.Inject

class APIServiceImpl @Inject constructor(
    private val moshi: Moshi,
    private val responseLoader: APIServiceResponseLoader
) : APIService {

    override fun getRoot(): String {
        val response = responseLoader.getRootResponse()

        return moshi.adapter(RootResponse::class.java).toJson(response)
    }

    override fun getLog(): String {
        val response = responseLoader.getLogResponse()

        val type: Type = Types.newParameterizedType(
            List::class.java,
            Log::class.java
        )

        return moshi.adapter<List<Log>>(type).toJson(response)
    }

    override fun getStatus(): String {
        val response = responseLoader.getStatus()

        return moshi.adapter(StatusResponse::class.java).toJson(response)
    }
}

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @Json(name = "ongoing") val ongoing: Boolean,
    @Json(name = "number") val phoneNumber: String? = null,
    @Json(name = "name") val contactName: String? = null,
)

@JsonClass(generateAdapter = true)
data class RootResponse(
    @Json(name = "start") val start: String,
    @Json(name = "services") val services: List<Service>,
)

@JsonClass(generateAdapter = true)
data class Service(
    @Json(name = "name") val name: String,
    @Json(name = "uri") val uri: String,
)

@JsonClass(generateAdapter = true)
data class Log(
    @Json(name = "beginning") val beginning: String,
    @Json(name = "duration") val durationInSeconds: String,
    @Json(name = "number") val phoneNumber: String,
    @Json(name = "name") val contactName: String,
    @Json(name = "timesQueried") val timesQueried: Int,
)