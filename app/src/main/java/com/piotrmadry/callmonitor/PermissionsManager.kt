package com.piotrmadry.callmonitor

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PermissionsManager @Inject constructor(
    @ActivityContext private val context: Context
) {

    fun hasPermission(
        resultLauncher: ActivityResultLauncher<String>,
        permission: String,
        requestRationale: () -> Unit
    ): Boolean = if (
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED
    ) {
        when {
            shouldShowRequestPermissionRationale(
                context as Activity,
                permission
            ) -> requestRationale()
            else -> resultLauncher.launch(permission)
        }
        false
    } else {
        true
    }
}