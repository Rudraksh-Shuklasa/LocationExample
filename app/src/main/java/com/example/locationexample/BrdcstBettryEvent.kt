package com.example.locationexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log

class BrdcstBettryEvent:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        val status: Int = p1!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug: Int = p1!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC


        Log.d("Battry","isCharging"+isCharging)

        Log.d("Battry","usbCharge"+usbCharge)

        Log.d("Battry","acCharge"+acCharge                       )


    }



}