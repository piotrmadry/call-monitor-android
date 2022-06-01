package com.piotrmadry.callmonitor

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import java.util.UUID
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
        while (cursor.moveToNext()) {
            logs.add(
                Log(
                    id = cursor.getString(beginningIndex),
                    beginning = cursor.getString(beginningIndex),
                    duration = cursor.getString(durationIndex),
                    number = cursor.getString(numberIndex),
                    name = getContactNameByPhoneNumber(cursor.getString(numberIndex)) ?: "Unknown",
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

        val contactsProjection = arrayOf(
            ContactsContract.PhoneLookup.DISPLAY_NAME
        )
    }

    private fun getContactNameByPhoneNumber(phoneNumber: String): String? {
        if (phoneNumber.isEmpty()) return null

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor: Cursor = contentResolver.query(
            uri,
            contactsProjection,
            null,
            null,
            null
        ) ?: return null

        val contactNameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)

        val contactName = if (cursor.moveToFirst()) {
            cursor.getString(contactNameIndex)
        } else {
            null
        }

        cursor.close()
        return contactName
    }
}


data class Log(
    val id: String,
    val beginning: String,
    val duration: String,
    val number: String,
    val name: String,
    val timesQueried: Int
)