package com.piotrmadry.callmonitor.datamodel

data class LogDataModel(
    val id: String,
    val beginning: String,
    val duration: String,
    val number: String,
    val name: String,
    val timesQueried: Int
)