package com.piotrmadry.httpserver

import fi.iki.elonen.NanoHTTPD

open class HttpServer(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {

//        when (session.uri) {
//            "/" ->
//        }
        val x = session
        return newFixedLengthResponse("Coś tam działa")
    }

    fun open() {
        start()
    }
}