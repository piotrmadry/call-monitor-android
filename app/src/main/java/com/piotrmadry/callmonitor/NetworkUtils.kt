package com.piotrmadry.callmonitor

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import javax.inject.Inject

class NetworkHelper @Inject constructor() {

    fun getLocalIPAddressWithPort(): String? {
        try {
            val networkInterfaces: Enumeration<NetworkInterface> =
                NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = networkInterfaces.nextElement()
                val inetAddresses: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress: InetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return "${inetAddress.getHostAddress()}:${Constants.ServerPort}"
                    }
                }
            }
        } catch (ex: SocketException) {
            return null
        }
        return null
    }
}