package com.example.locationexample


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class GeoFencingExample : AppCompatActivity(), OnMapReadyCallback {


    private var geofencingClient: GeofencingClient? = null
    private var mMap: GoogleMap? = null
    private val requestId = "GeoTrIntentService"

    private lateinit var mMap2: GoogleMap
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallbacks: LocationsCallbacks
    private var marker: MarkerOptions? = null
    private var m1: Marker? = null

    var TAG: String = this.javaClass.getSimpleName()

    @SuppressLint("RestrictedApi", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_fencing_example)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        locationCallbacks = LocationsCallbacks()
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(2000)
        locationRequest.setFastestInterval(2000)

        fusedLocationProvider = FusedLocationProviderClient(this)
        fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallbacks, Looper.getMainLooper())

    }

    inner class LocationsCallbacks : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            Log.d(TAG, "On Location Result")
            var lat = p0?.lastLocation?.latitude as Double
            var long = p0?.lastLocation?.longitude as Double

        }

        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
        }
    }

    private var geoFenceLimits: Circle? = null

    fun drawGeoCircle() {
        Log.d(TAG, "m1" + m1?.position?.latitude)
        val circleOptions = CircleOptions()
            .center(m1?.getPosition())
            .strokeColor(Color.argb(50, 70, 70, 70))
            .fillColor(Color.argb(100, 150, 150, 150))
            .strokeWidth(2f)
            .radius(1000.0)
        geoFenceLimits = mMap?.addCircle(circleOptions)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true


        geofencingClient = LocationServices.getGeofencingClient(this)
        m1 = mMap?.addMarker(MarkerOptions().title("Center").position(LatLng(67.0, -145.0)))
        drawGeoCircle()

        mMap?.setOnMarkerClickListener(object  :GoogleMap.OnMarkerClickListener{
            override fun onMarkerClick(p0: Marker?): Boolean {
                Toast.makeText(applicationContext, "Clicked ", Toast.LENGTH_SHORT).show();
                return false
            }
        })

        var geofence = Geofence.Builder().setRequestId(requestId).setCircularRegion(23.0661415, 72.5298091, 1000f)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
            .setExpirationDuration(100000000)
            .setLoiteringDelay(1000)
            .build()

//        var geofencingRequest = GeofencingRequest.Builder().addGeofence(geofence).build()
        var geofencingRequest = GeofencingRequest.Builder().addGeofences(arrayListOf(geofence)).build()


        var intent = Intent(this, GeofenceTrasitionService::class.java)
        var pendingIntent: PendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        geofencingClient?.addGeofences(geofencingRequest, pendingIntent)

    }

}