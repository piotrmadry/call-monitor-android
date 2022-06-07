package com.piotrmadry.httpserver

interface APIService {
    fun getRoot(): String
    fun getLog(): String
    fun getStatus(): String
}