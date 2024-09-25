package com.example.gps_tracker

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (it[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || it[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true){

                getUserLocation()
            }else {
                //show explanation

                showDialog(

                    "Kindly Notice : This feature will be disabled",

                    "OK",

                    onPositiveClickListner = { dialog, which -> dialog?.dismiss() },
                    null,
                    null
                )
            }
        }


    var marker : Marker? = null
    var googleMap : GoogleMap?=null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isPermissionAllowed(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
            isPermissionAllowed(android.Manifest.permission.ACCESS_FINE_LOCATION)){

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)

            getUserLocation()

        }else{
            requestPermissionFromUser()

        }
    }

    fun requestPermissionFromUser(){

        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION ) ||
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)){


            showDialog(
                "We need to access your location because without location we can't get the nearest drivers " ,
                "I understand",
                onPositiveClickListner = { dialog, which ->
                    permissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,

                            )

                    )
                    dialog?.dismiss()

                },
                "Cancel",
                onNegativeClickListner = { dialog, which ->
                    dialog?.dismiss()

                }
            )
        }else{
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,

                    )

            )
        }

    }


    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations){
                Log.e("TAG-update", "onLocationResult :${location.latitude}")
                Log.e("TAG-update", "onLocationResult :${location.longitude}")

                val latLng = LatLng(location.latitude,location.longitude)
                putMarkerOnMap(latLng)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun getUserLocation(){

        val currentLocationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()


        fusedLocationProviderClient.lastLocation.addOnSuccessListener{

            Log.e("TAG", "getUserLocation :${it.latitude}")
            Log.e("TAG", "getUserLocation :${it.longitude}")
            Log.e("TAG", "getUserLocation : speed = ${it.speed}")


        }

        fusedLocationProviderClient.getCurrentLocation(currentLocationRequest, null).addOnSuccessListener{
            Log.e("TAG-current", "getUserLocation :${it.latitude}")
            Log.e("TAG-current", "getUserLocation :${it.longitude}")
            Log.e("TAG-current", "getUserLocation : speed = ${it.speed}")
        }


        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,8000).build()

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper() )

    }



    fun putMarkerOnMap(latLng: LatLng){


        if (marker==null){
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Location")
            marker = googleMap?.addMarker(markerOptions)
        } else
            marker?.position = latLng

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16F))
    }

    fun isPermissionAllowed(permission :String ) : Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            permission

        ) ==  PackageManager.PERMISSION_GRANTED

    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        this.googleMap = googleMap

    }
}