package com.piotrmadry.callmonitor

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import javax.inject.Inject

class CallHistory @Inject constructor(
    private val contentResolver: ContentResolver
) {
    companion object {
        val logProjection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME
        )

        val contactsProjection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME
        )
    }

    fun getLog(): List<Log> {
        val logs = mutableListOf<Log>()

        val cursor: Cursor = getCallLogCursor() ?: return logs

        val beginningIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

        while (cursor.moveToNext()) {
            logs.add(
                Log(
                    id = cursor.getString(beginningIndex),
                    beginning = cursor.getString(beginningIndex),
                    duration = cursor.getString(durationIndex),
                    number = cursor.getString(numberIndex),
                    name = getContactNameByPhoneNumber(cursor.getString(numberIndex)) ?: "Unknown",
                    timesQueried = 1
                )
            )
        }
        cursor.close()
        return logs.toList()
    }

    suspend fun getLogCompact(): List<LogCompact> {
        val logs = mutableListOf<LogCompact>()

        val cursor: Cursor = getCallLogCursor() ?: return logs

        val beginningIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

        while (cursor.moveToNext()) {
            logs.add(
                LogCompact(
                    id = cursor.getString(beginningIndex),
                    contactName = getContactNameByPhoneNumber(cursor.getString(numberIndex)) ?: "Unknown",
                    durationInSeconds = cursor.getString(durationIndex)
                )
            )
        }
        cursor.close()
        return logs.toList()
    }

    private fun getContactNameByPhoneNumber(phoneNumber: String): String? {
        if (phoneNumber.isEmpty()) return null
        val cursor: Cursor = getContactByPhoneNumberCursor(phoneNumber) ?: return null

        val contactNameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)

        val contactName = if (cursor.moveToFirst()) cursor.getString(contactNameIndex) else null

        cursor.close()
        return contactName
    }

    private fun getCallLogCursor(): Cursor? {
        return contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            logProjection,
            null,
            null,
            null
        )
    }

    private fun getContactByPhoneNumberCursor(phoneNumber: String): Cursor? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        return contentResolver.query(
            uri,
            contactsProjection,
            null,
            null,
            null
        )
    }
}


data class LogCompact(
    val id: String,
    val contactName: String,
    val durationInSeconds: String
)

data class Log(
    val id: String,
    val beginning: String,
    val duration: String,
    val number: String,
    val name: String,
    val timesQueried: Int
)