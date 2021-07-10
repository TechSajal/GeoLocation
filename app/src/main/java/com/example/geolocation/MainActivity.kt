 package com.example.geolocation

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import java.util.jar.Manifest

 class MainActivity : AppCompatActivity(),OnMapReadyCallback {
     private var mMap :GoogleMap? = null
     lateinit var mapView:MapView
     private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
     override fun onMapReady(googleMap : GoogleMap) {
      mapView.onResume()
         mMap = googleMap
         if (ActivityCompat.checkSelfPermission(
                 this,
                 android.Manifest.permission.ACCESS_FINE_LOCATION
         ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 this,
                 android.Manifest.permission.ACCESS_COARSE_LOCATION
         ) == PackageManager.PERMISSION_GRANTED)
         {
             return
         }else{
             ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),11)
         }
         mMap!!.isMyLocationEnabled = false
     }
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         var mapviewbundle:Bundle? = null
         if (savedInstanceState!=null){
             mapviewbundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
         }
         mapView = findViewById(R.id.map1)
         mapView.onCreate(mapviewbundle)
         mapView.getMapAsync(this)
    }

     override fun onSaveInstanceState(outState: Bundle) {
         super.onSaveInstanceState(outState)
       //  askGalleryPermitionsLocation()
         var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
         if (mapViewBundle == null){
             mapViewBundle = Bundle()
             outState.putBundle(MAP_VIEW_BUNDLE_KEY,mapViewBundle)
         }
         mapView.onSaveInstanceState(mapViewBundle)
     }

     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          if (requestCode == 11){
                if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                }else{
                    Toast.makeText(this,"Please accept our Permission",Toast.LENGTH_LONG).show()
                }
          }
     }


 }