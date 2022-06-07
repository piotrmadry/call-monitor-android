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

    @Inject
    lateinit var impl: APIServiceImpl

    private val viewModel: MainActivityViewModel by viewModels()

    private val recyclerViewAdapter = CallMonitorRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        server = HttpServer(port = Constants.ServerPort, service = impl)
        server.start()

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

        if (checkAndRequestReadCallLogPermission() && checkAndRequestReadContactPermission()) {
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

    private fun openAppSettings() =
        startActivity(IntentUtils.getAppSettingsIntent(this))

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}