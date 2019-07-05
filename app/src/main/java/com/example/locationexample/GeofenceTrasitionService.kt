package com.example.locationexample

import android.app.IntentService
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesUtil.getErrorString
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.media.RingtoneManager
import android.media.Ringtone
import android.R.attr.start
import android.media.MediaPlayer









class GeofenceTrasitionService : IntentService("Geo") {


        var TAG: String = this.javaClass.getSimpleName()



        override fun onHandleIntent(intent: Intent?) {
            Log.d(TAG, "onHandleIntent")

            var geoFencingEvent = GeofencingEvent.fromIntent(intent)

            if (geoFencingEvent.hasError()) {
                val errorMsg = getErrorString(geoFencingEvent.getErrorCode())
                Log.e(TAG, errorMsg)
                return
            }

            if (geoFencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geoFencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
                || geoFencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
            ) {
                // 2
                val geofenceList = geoFencingEvent.triggeringGeofences

                var status = ""
                val triggeringGeofencesList: ArrayList<String> = ArrayList()

                for (geofence in geofenceList) {
                    triggeringGeofencesList.add(geofence.getRequestId())
                }

                Log.d(TAG, "triggeringGeofencesList size" + triggeringGeofencesList.size)


                status = TextUtils.join(", ", triggeringGeofencesList)

                sendNotification(status)
            }


    }

         fun sendNotification(status: String?) {

            Log.d("TAG" ,"SEND notificaition" + status)
            Log.d("TAG" ,"Current id" + Thread.currentThread().id)

             val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
             val player = MediaPlayer.create(this, notification)
             player.isLooping = true
             player.start()
        }
}
