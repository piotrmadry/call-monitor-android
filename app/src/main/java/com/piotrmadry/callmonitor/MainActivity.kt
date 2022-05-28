package com.piotrmadry.callmonitor

import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.piotrmadry.httpserver.HttpServer


class MainActivity : AppCompatActivity() {

    private lateinit var server: HttpServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        server = HttpServer(port = 12345)

        server.start()

        findViewById<TextView>(R.id.ip)?.text = getWifiIpAddress()
    }

    fun getWifiIpAddress(): String? {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return Formatter.formatIpAddress(info.ipAddress)
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }
}