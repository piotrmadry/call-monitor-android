package com.piotrmadry.callmonitor

import android.Manifest
import android.app.AlertDialog
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.piotrmadry.callmonitor.databinding.ActivityMainBinding
import com.piotrmadry.httpserver.HttpServer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var server: HttpServer

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var callHistory: CallHistory

    @Inject
    lateinit var permissionManager: PermissionsManager

    private val recyclerViewAdapter = CallMonitorRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        server = HttpServer(port = 12345)
        server.start()

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        if (checkAndRequestReadCallLogPermission()) {
            getCallsHistory()
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
        if (hasPermission) getCallsHistory()
    }

    private fun checkAndRequestReadCallLogPermission() = permissionManager.hasPermission(
        resultLauncher = permissionResult,
        permission = Manifest.permission.READ_CALL_LOG,
        requestRationale = rationaleDialog::show
    )

    private fun openAppSettings() =
        startActivity(IntentUtils.getAppSettingsIntent(this))

    private fun getCallsHistory() {
        recyclerViewAdapter.setItems(
            listOf(ServerInfoItem(ipAddress = getWifiIpAddress())) +
                callHistory.getLog().map { CallItem(id = it.id, name = it.name) })
    }

    private fun getWifiIpAddress(): String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return Formatter.formatIpAddress(info.ipAddress) + ":12345"
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}