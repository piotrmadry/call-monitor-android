package com.piotrmadry.callmonitor

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import javax.inject.Inject

class CallHistory @Inject constructor(
    private val contentResolver: ContentResolver
) {

    fun getLog(): List<Log> {
        val logs = mutableListOf<Log>()
        val cursor: Cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            null
        ) ?: return logs

        val beginningIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
        val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
        while (cursor.moveToNext()) {
            logs.add(
                Log(
                    beginning = cursor.getString(beginningIndex),
                    duration = cursor.getString(durationIndex),
                    number = cursor.getString(numberIndex),
                    name = "Unknown",
                    1
                )
            )
        }
        cursor.close()
        return logs.toList()
    }

    companion object {
        val projection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME
        )
    }
}


data class Log(
    val beginning: String,
    val duration: String,
    val number: String,
    val name: String,
    val timesQueried: Int
)