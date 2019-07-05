package com.example.locationexample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    lateinit var btnStartupdate: Button
    lateinit var btnStopUpdates: Button
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartupdate = findViewById(R.id.btn_start_upds)
        btnStopUpdates = findViewById(R.id.btn_stop_upds)
        txtLat = findViewById(R.id.txtLat);
        txtLong = findViewById(R.id.txtLong);
        txtTime = findViewById(R.id.txtTime);
        mLocationRequest = LocationRequest()

        val intent = Intent(this@MainActivity, GetLocationServices::class.java)
        intent.setAction("ACTION_START_FOREGROUND_SERVICE")
        startService(intent)

        val locationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(Context.LOCATION_SERVICE)){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        , 11)
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                    finish()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        btnStartupdate.setOnClickListener {
            if(checkPermissionForLocation(this))
            {
                startLocationUpdates()
            }
        }
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have to add startlocationUpdate() method later instead of Toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected fun startLocationUpdates(){

            mLocationRequest!!.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest!!.interval=INTERVAL
            mLocationRequest.fastestInterval=FASTEST_INTERVAL

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                return
            }

            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())


        }




    private val mLocationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            p0!!.lastLocation
            onLocationChange(p0.lastLocation)

        }
    }

    fun onLocationChange(location : Location){
        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        txtTime.text = "Updated at : " + sdf.format(date)
        txtLat.text = "LATITUDE : " + mLastLocation.latitude
        txtLong.text = "LONGITUDE : " + mLastLocation.longitude
    }
}

