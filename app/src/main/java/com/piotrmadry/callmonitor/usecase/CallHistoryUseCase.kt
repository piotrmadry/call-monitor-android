package com.piotrmadry.callmonitor.usecase

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.piotrmadry.callmonitor.datamodel.LogCompactDataModel
import com.piotrmadry.callmonitor.datamodel.LogDataModel
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import javax.inject.Inject

class CallHistoryUseCase @Inject constructor(
    private val contentResolver: ContentResolver,
    private val appPreferences: AppSharedPreferences
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

    fun getLog(): List<LogDataModel> {

        val appStartMs = appPreferences.getServerStartMs()

        val (selection, args) = if (appStartMs != -1L) {
            CallLog.Calls.DATE + ">=?" to arrayOf(appStartMs.toString())
        } else null to null

        val logs = mutableListOf<LogDataModel>()

        val cursor: Cursor = getCallLogCursor(
            selection = selection,
            selectionArgs = args
        ) ?: return logs

        val beginningIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

        while (cursor.moveToNext()) {
            logs.add(
                LogDataModel(
                    id = cursor.getString(beginningIndex),
                    beginning = cursor.getLong(beginningIndex),
                    duration = cursor.getString(durationIndex),
                    number = cursor.getString(numberIndex),
                    name = getContactNameByPhoneNumber(cursor.getString(numberIndex)) ?: "Unknown",
                    timesQueried = 1
                )
            )
        }
        cursor.close()
        return logs.reversed()
    }

    fun getLogCompact(): List<LogCompactDataModel> {
        val logs = mutableListOf<LogCompactDataModel>()

        val cursor: Cursor = getCallLogCursor() ?: return logs

        val beginningIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)

        while (cursor.moveToNext()) {
            logs.add(
                LogCompactDataModel(
                    id = cursor.getString(beginningIndex),
                    contactName = getContactNameByPhoneNumber(cursor.getString(numberIndex)) ?: "Unknown",
                    durationInSeconds = cursor.getString(durationIndex)
                )
            )
        }
        cursor.close()
        return logs.reversed()
    }

    fun getContactNameByPhoneNumber(phoneNumber: String): String? {
        if (phoneNumber.isEmpty()) return null
        val cursor: Cursor = getContactByPhoneNumberCursor(phoneNumber) ?: return null

        val contactNameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)

        val contactName = if (cursor.moveToFirst()) cursor.getString(contactNameIndex) else null

        cursor.close()
        return contactName
    }

    private fun getCallLogCursor(
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): Cursor? {
        return contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            logProjection,
            selection,
            selectionArgs,
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


