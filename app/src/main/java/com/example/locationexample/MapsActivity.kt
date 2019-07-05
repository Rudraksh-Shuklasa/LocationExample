package com.example.locationexample

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.provider.SyncStateContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(),
                            OnMapReadyCallback,
                            GoogleMap.OnMarkerDragListener,
                            GoogleMap.OnInfoWindowClickListener
 {


        private lateinit var mMap: GoogleMap
        private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
        private val INTERVAL: Long = 20000000
        private val FASTEST_INTERVAL: Long = 10000000
        lateinit var sydney : LatLng
        private var geoFenceMarker: Marker? = null
        lateinit var mLastLocation: Location
        var main=MarkerOptions()
        internal lateinit var mLocationRequest: LocationRequest
        var markerClicked: Boolean = false
        lateinit var geofencingClient: GeofencingClient
        var  geofenceList = ArrayList<String>()

        var list= arrayListOf<String>("sad","bad","dad")
        private val GEO_DURATION = (60 * 60 * 1000).toLong()
        private val GEOFENCE_REQ_ID = "My Geofence"
        private val GEOFENCE_RADIUS = 500.0f // in meters

    @SuppressLint("MissingPermission", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mLocationRequest = LocationRequest()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationProviderClient= FusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)



        mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())

    }



    private val mLocationCallback= object : LocationCallback(){
        @SuppressLint("MissingPermission")
        override fun onLocationResult(p0: LocationResult?) {
            p0!!.lastLocation

            if(main != null)
            {
               mMap.clear()
            }

            sydney = LatLng(p0!!.lastLocation.latitude, p0.lastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15F));

            var info=InfoWndowAdapter(this@MapsActivity,list)
            mMap.setInfoWindowAdapter(info)
            mMap.animateCamera(CameraUpdateFactory.zoomIn());


            mMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null);

            var camera=CameraPosition.Builder()
                .target(sydney)
                .zoom(17F)
                .bearing(90F)
                .tilt(30F)
                .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))


            main?.draggable(true)
            var address = getCompleteAddressString(p0!!.lastLocation.latitude, p0.lastLocation.longitude)
            var m =mMap.addMarker(main.position(sydney).title(address).snippet("0"))

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            mMap.setOnInfoWindowClickListener(this@MapsActivity)

            mMap.isMyLocationEnabled =true

            var geofence = Geofence.Builder().setRequestId("23").setCircularRegion(67.0, -145.0, 1000f)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setExpirationDuration(100000000)
                .setLoiteringDelay(50000)
                .build()



//        var geofencingRequest = GeofencingRequest.Builder().addGeofence(geofence).build()
            var geofencingRequest = GeofencingRequest.Builder().addGeofences(arrayListOf(geofence)).build()


            var intent = Intent(this@MapsActivity, GeofenceTrasitionService::class.java)
            var pendingIntent: PendingIntent = PendingIntent.getService(this@MapsActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            geofencingClient?.addGeofences(geofencingRequest, pendingIntent)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mLocationRequest!!.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval=INTERVAL
        mLocationRequest.fastestInterval=FASTEST_INTERVAL
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mMap.isTrafficEnabled=true
        markerClicked = false

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener {

            Log.d("tag" ,"last location")

            sydney = LatLng(it.latitude, it.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15F));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.2098,72.8478), 15F));


            mMap.animateCamera(CameraUpdateFactory.zoomIn());


            mMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null);


            var camera=CameraPosition.Builder()
                .target(sydney)
                .zoom(17F)
                .bearing(90F)
                .tilt(30F)
                .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))


            var address = getCompleteAddressString(it.latitude, it.longitude)
            var marker=MarkerOptions()
            marker.draggable(true)
            marker.title(address)
            marker.position(sydney)
        }




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

     override fun onMarkerDragEnd(p0: Marker?) {
         Log.d("Marker","star")
     }

     override fun onMarkerDragStart(p0: Marker?) {
       Log.d("Marker","star")
     }

     override fun onMarkerDrag(p0: Marker?) {
         Log.d("Marker","star")
     }

     override fun onInfoWindowClick(p0: Marker?) {
       Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
     }


 }

