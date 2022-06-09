package com.piotrmadry.callmonitor

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.piotrmadry.callmonitor.adapter.CallMonitorRecyclerViewAdapter
import com.piotrmadry.callmonitor.databinding.ActivityMainBinding
import com.piotrmadry.callmonitor.server.APIServiceImpl
import com.piotrmadry.callmonitor.storage.AppSharedPreferences
import com.piotrmadry.callmonitor.usecase.CallHistoryUseCase
import com.piotrmadry.callmonitor.utils.DateUtils
import com.piotrmadry.callmonitor.utils.IntentUtils
import com.piotrmadry.callmonitor.utils.PermissionUtils
import com.piotrmadry.httpserver.Server
import com.piotrmadry.httpserver.HttpServer
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var server: HttpServer

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var callHistoryUseCase: CallHistoryUseCase

    @Inject
    lateinit var permissionManager: PermissionUtils

    @Inject
    lateinit var impl: APIServiceImpl

    @Inject
    lateinit var date: DateUtils

    @Inject
    lateinit var pref: AppSharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private val recyclerViewAdapter = CallMonitorRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        server = HttpServer(port = Server.Port, service = impl)
        server.start().run { pref.storeServerStartMs(System.currentTimeMillis()) }

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }

        viewModel.items.observe(this) {
            recyclerViewAdapter.setItems(it)
        }

        viewModel.progress.observe(this) { isVisible ->
            with(binding) {
                progressBar.isVisible = isVisible
                recyclerview.isVisible = !isVisible
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (checkAndRequestReadCallLogPermission()
            && checkAndRequestReadContactPermission()
            && checkAndRequestPhoneStatePermission()
        ) {
            viewModel.getData()
        }
    }

    private val rationaleDialog: AlertDialog.Builder by lazy {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.main_activity_rationale_dialog_alert_title))
            .setMessage(getString(R.string.main_activity_rationale_dialog_alert_description))
            .setPositiveButton(
                getString(R.string.main_activity_rationale_dialog_alert_positive_btn_text)
            ) { dialog, _ ->
                openAppSettings()
                dialog?.dismiss()
            }
            .setNegativeButton(getString(R.string.main_activity_rationale_dialog_alert_negative_btn_text)) { _, _ ->
                finish()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
    }

    private val permissionResult: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission ->
        if (hasPermission) viewModel.getData()
    }

    private fun checkAndRequestReadCallLogPermission() = permissionManager.hasPermission(
        resultLauncher = permissionResult,
        permission = Manifest.permission.READ_CALL_LOG,
        requestRationale = rationaleDialog::show
    )

    private fun checkAndRequestReadContactPermission() = permissionManager.hasPermission(
        resultLauncher = permissionResult,
        permission = Manifest.permission.READ_CONTACTS,
        requestRationale = rationaleDialog::show
    )

    private fun checkAndRequestPhoneStatePermission() = permissionManager.hasPermission(
        resultLauncher = permissionResult,
        permission = Manifest.permission.READ_PHONE_STATE,
        requestRationale = rationaleDialog::show
    )

    private fun openAppSettings() =
        startActivity(IntentUtils.getAppSettingsIntent(this))

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}