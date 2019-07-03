package com.example.locationexample

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationManager
import android.util.Log
import android.location.Geocoder
import java.util.*


class LocationListner : AppCompatActivity(), LocationListener {
    var mLocationManager: LocationManager? = null
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onLocationChanged(p0: Location?) {
        Log.d("lat", p0!!.latitude.toString())
        Log.d("log", p0!!.longitude.toString())
        Log.i("as", p0!!.accuracy.toString())
        getCompleteAddressString(p0!!.latitude,p0!!.longitude)
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_listner)
        mLocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
      mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10F,this)




    }


    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.d("My Current", strReturnedAddress.toString())
            } else {
                Log.d("My Current", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("My Current", "Canont get Address!")
        }

        return strAdd
    }
}

