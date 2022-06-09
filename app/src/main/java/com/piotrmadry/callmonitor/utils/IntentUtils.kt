package com.piotrmadry.callmonitor.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object IntentUtils {

    fun getAppSettingsIntent(context: Context) = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
}