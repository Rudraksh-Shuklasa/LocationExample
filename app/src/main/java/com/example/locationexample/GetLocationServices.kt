package com.example.locationexample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import android.graphics.Bitmap
import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat

import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.android.gms.location.*


class GetLocationServices : Service(){
    private var mManager: NotificationManager? = null
    val ANDROID_CHANNEL_ID = "com.chikeandroid.tutsplustalerts.ANDROID"
    val ANDROID_CHANNEL_NAME = "ANDROID CHANNEL"

    val mHandler = Handler()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val requestId = "GeoTrIntentService"
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallbacks: GeoFencingExample.LocationsCallbacks
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show()

        val androidChannel = NotificationChannel(
            ANDROID_CHANNEL_ID,
            ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )

        androidChannel.enableLights(true)

        androidChannel.enableVibration(true)

        androidChannel.lightColor = Color.GREEN

        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

       getManager().createNotificationChannel(androidChannel)


    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mLocationRequest = LocationRequest()
        val icon = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_dialog_dialer
        )

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationRequest!!.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval=INTERVAL
        mLocationRequest.fastestInterval=FASTEST_INTERVAL
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProvider = FusedLocationProviderClient(this)
        fusedLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, mHandler.looper)

        Toast.makeText(this, "Creating Notification", Toast.LENGTH_SHORT).show()

        val notification = Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
            .setContentTitle("AndroidMonks Sticker")
            .setTicker("AndroidMonks Sticker")
            .setContentText("Example")
            .setSmallIcon(R.drawable.ic_dialog_dialer)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setOngoing(true).build()

        startForeground(1001, notification)
        return Service.START_STICKY
    }

    private val mLocationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            p0!!.lastLocation
            onLocationChange(p0.lastLocation)

        }
    }

    fun onLocationChange(location : Location){
        mLastLocation = location
        Log.d("GetLocation : " , mLastLocation.latitude.toString())
        Log.d( "GetLocation : " , mLastLocation.longitude.toString())
    }

    override fun onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show()
    }

    private fun getManager(): NotificationManager {
        if (mManager == null) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager as NotificationManager
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}