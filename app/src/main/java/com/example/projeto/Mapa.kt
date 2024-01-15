package com.example.projeto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.map.display.ui.UiComponentClickListener

class Mapa : AppCompatActivity() {
    val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var mapFragment: MapFragment
    private lateinit var tomTomMap: TomTomMap
    private lateinit var locationProvider: LocationProvider
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            showLocation()
        } else {
            Toast.makeText(
                this,
                getString(R.string.location_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        iniciarMapa()
        iniciarLocationProvider()
    }

    fun iniciarMapa() {

        val mapOptions = MapOptions(mapKey = apiKey)
        mapFragment = MapFragment.newInstance(mapOptions)

        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.getMapAsync { map ->
            tomTomMap = map
            enableUserLocation()
        }

    }

    private fun iniciarLocationProvider() {
        locationProvider = AndroidLocationProvider(this)
    }

    private fun enableUserLocation() {
        if (areLocationPermissionsGranted()) {
            showLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun showLocation() {
        locationProvider.enable()


        onLocationUpdateListener = OnLocationUpdateListener { location ->
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 12.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }

        val extras = intent.extras
        if (extras != null && extras.containsKey("local")) {
            val placePosition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("local", GeoPoint::class.java)
            } else {
                extras.getParcelable("local")
            }
            if (placePosition != null) {
                tomTomMap.moveCamera(CameraOptions(placePosition, zoom = 12.0))
                val uiComponentClickListener = UiComponentClickListener {
                    locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
                    tomTomMap.setLocationProvider(locationProvider)
                    val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
                    tomTomMap.enableLocationMarker(locationMarker)
                }
                mapFragment.currentLocationButton.addCurrentLocationButtonClickListener(
                    uiComponentClickListener
                )
                val markerOptions = MarkerOptions(
                    coordinate = placePosition,
                    pinImage = ImageFactory.fromResource(R.drawable.ic_marker_pin)
                )
                tomTomMap.addMarker(markerOptions)
            }
        } else {
            locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
            tomTomMap.setLocationProvider(locationProvider)
            val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
            tomTomMap.enableLocationMarker(locationMarker)
        }
        tomTomMap.showTrafficFlow()
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        tomTomMap.setLocationProvider(null)
        super.onDestroy()
        locationProvider.close()
    }

}