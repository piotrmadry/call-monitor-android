package com.piotrmadry.callmonitor.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class DateUtils @Inject constructor() {

    companion object {
        const val DateTimeWithTimeZoneFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
    }

    fun toDateTimeWithTimeZone(timestamp: Long, locale: Locale): String {
        val format = SimpleDateFormat(DateTimeWithTimeZoneFormat, locale)
        format.timeZone = TimeZone.getDefault()
        return format.format(timestamp)
    }
}