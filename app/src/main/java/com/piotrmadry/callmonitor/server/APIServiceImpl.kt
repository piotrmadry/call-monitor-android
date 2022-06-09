package com.piotrmadry.callmonitor.server

import com.piotrmadry.callmonitor.response.Log
import com.piotrmadry.callmonitor.response.RootResponse
import com.piotrmadry.callmonitor.response.StatusResponse
import com.piotrmadry.httpserver.APIService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import javax.inject.Inject

class APIServiceImpl @Inject constructor(
    private val moshi: Moshi,
    private val dataLoader: APIServiceDataLoader
) : APIService {

    override fun getRoot(): String {
        val response = dataLoader.getRootResponse()

        return moshi.adapter(RootResponse::class.java).toJson(response)
    }

    override fun getLog(): String {
        val response = dataLoader.getLogResponse()

        val type: Type = Types.newParameterizedType(
            List::class.java,
            Log::class.java
        )

        return moshi.adapter<List<Log>>(type).toJson(response)
    }

    override fun getStatus(): String {
        val response = dataLoader.getStatusResponse()

        return moshi.adapter(StatusResponse::class.java).toJson(response)
    }
}

