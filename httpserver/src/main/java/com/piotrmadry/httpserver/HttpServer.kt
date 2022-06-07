package com.piotrmadry.httpserver

import fi.iki.elonen.NanoHTTPD

open class HttpServer(port: Int, private val service: APIService) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/" -> newFixedLengthResponse(
                Response.Status.OK,
                MimeTypes.ApplicationJson,
                service.getRoot()
            )
            "/status" -> newFixedLengthResponse(
                Response.Status.OK,
                MimeTypes.ApplicationJson,
                service.getStatus()
            )
            "/log" -> newFixedLengthResponse(
                Response.Status.OK,
                MimeTypes.ApplicationJson,
                service.getLog()
            )
            else -> newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                MimeTypes.PlainText,
                "Not found"
            )
        }
    }
}