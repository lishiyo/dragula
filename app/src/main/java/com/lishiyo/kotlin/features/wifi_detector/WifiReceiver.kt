package com.lishiyo.kotlin.features.wifi_detector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.Toast


/**
 * Created by connieli on 6/9/17.
 */
class ConnectionReceiver(wifiManager: WifiManager) : BroadcastReceiver() {
    val wifiManager = wifiManager

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val results = wifiManager.scanResults
                val bestSignal: ScanResult? = results.asSequence().reduce({ memo, currentSignal ->
                    val signalCompare = WifiManager.compareSignalLevel(memo.level, currentSignal.level)
                    if (signalCompare < 0) currentSignal else memo
                })

                val message: String
                if (bestSignal != null) {
                    message = String.format("%s networks found. %s is the strongest.",
                            results.size, bestSignal!!.SSID)
                } else {
                    message = "could not find any networks " + results.size
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

//        intent?.let {
//            val noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
//            val reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON)
//            val isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false)
//
//            val currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo
//            val otherNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO) as NetworkInfo
//
//            if (currentNetworkInfo.isConnected) {
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show()
//            }
//        }

    }

}