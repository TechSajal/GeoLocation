 package com.example.geolocation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

 class MainActivity : AppCompatActivity(),OnMapReadyCallback {
     private var mMap :GoogleMap? = null
     lateinit var mapView:MapView
     private lateinit var search :Button
     private lateinit var B_clear:Button
     private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
     private val DEFAULT_ZOOM = 15f
     lateinit var tvCurrentAddress: TextView
     private var fusedLocationProviderClient: FusedLocationProviderClient? = null
     var end_latitude = 0.0
     var end_longitude = 0.0
     var origin: MarkerOptions? = null
     var destination: MarkerOptions? = null
     var latitude = 0.0
     var longitude = 0.0
    private var msm:Marker?=null
     var mtm:ArrayList<Marker> =ArrayList()
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

             getCurrentLocation()


         }else{
             ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),11)
         }
         mMap!!.isMyLocationEnabled = false
     }
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         var mapviewbundle:Bundle? = null
         tvCurrentAddress = findViewById(R.id.tvAdd)
         if (savedInstanceState!=null){
             mapviewbundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
         }
         mapView = findViewById(R.id.map1)
         mapView.onCreate(mapviewbundle)
         mapView.getMapAsync(this)
          B_clear  = findViewById(R.id.B_clear)
          search  = findViewById(R.id.B_search)
         search.setOnClickListener {
             searchArea()
         }

         B_clear.setOnClickListener {
             mapView.onCreate(mapviewbundle)
             mapView.getMapAsync(this)
             tvCurrentAddress.text = ""
         }
    }
      private fun remo(){
          for (Marker in mtm){
               Marker.remove()
          }
      }

     private fun resem(){
         if (msm!=null){
             msm!!.isVisible =true
             msm = null
             remo()
         }
     }

     private fun searchArea() {
         val tf_location = findViewById<View>(R.id.TF_location) as EditText
         val location = tf_location.text.toString()
         var addressList: List<Address>? = null
         val markerOptions = MarkerOptions()
         if (location != "") {
             val geocoder = Geocoder(applicationContext)
             try {
                 addressList = geocoder.getFromLocationName(location, 5)
             } catch (e: IOException) {
                 e.printStackTrace()
             }
             if (addressList != null) {
                 for (i in addressList.indices) {
                     val myAddress = addressList[i]
                     val latLng = LatLng(myAddress.latitude, myAddress.longitude)
                     markerOptions.position(latLng)
                     mMap!!.addMarker(markerOptions)
                     end_latitude = myAddress.latitude
                     end_longitude = myAddress.longitude
                     mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                     val mo = MarkerOptions()
                     mo.title("Distance")
                     val results = FloatArray(10)
                     Location.distanceBetween(
                         latitude,
                         longitude,
                         end_latitude,
                         end_longitude,
                         results
                     )
                     // convert m to km
                     val s = String.format("%.1f", results[0] / 1000)

                     //Setting marker to draw route between these two points
                     origin = MarkerOptions().position(LatLng(latitude, longitude))
                         .title("HSR Layout").snippet("origin")
                     destination = MarkerOptions().position(LatLng(end_latitude, end_longitude))
                             .title(tf_location.text.toString())
                             .snippet("Distance = $s KM")
                     resem()
                     mMap!!.addMarker(destination)
                     mMap!!.addMarker(origin)
                     val o = LatLng(latitude,longitude)
                     val d = LatLng(end_latitude,end_longitude)
                     Toast.makeText(
                         this@MainActivity,
                         "Distance = $s KM",
                         Toast.LENGTH_SHORT
                     ).show()

                     tvCurrentAddress.text = "Distance = $s KM"
                   //  val dir = getDirectionURL(destination,origin)
                    mMap!!.addPolyline(PolylineOptions()
                        .color(Color.RED)
                        .add(o)
                        .add(d)
                        .width(8f))
                     mMap!!.addCircle(CircleOptions().center(o).radius(500.0).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.argb(70,150,50,50)))



                 } } } }



     override fun onSaveInstanceState(outState: Bundle) {
         super.onSaveInstanceState(outState)
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
                      getCurrentLocation()
                }else{
                    Toast.makeText(this,"Please accept our Permission",Toast.LENGTH_LONG).show()
                }
          }
     }

     private fun getCurrentLocation() {
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
         try {
             @SuppressLint("MissingPermission")
             val location = fusedLocationProviderClient!!.lastLocation
             location.addOnCompleteListener(object : OnCompleteListener<Location> {
                 override fun onComplete(loc: Task<Location>) {
                     if (loc.isSuccessful) {
                         val currentLocation = loc.result
                         if (currentLocation != null) {
                             moveCamera(
                                 LatLng(currentLocation.latitude, currentLocation.longitude),
                                 DEFAULT_ZOOM
                             )
                              latitude = currentLocation.latitude
                             longitude=currentLocation.longitude

                         }

                     } else {
                         Toast.makeText(this@MainActivity,"Current Location Not Found",Toast.LENGTH_LONG).show()
                     }
                 }

                 @SuppressLint("MissingPermission")
                 private fun moveCamera(latLng: LatLng, zoom: Float) {
                     mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
                     mMap!!.isMyLocationEnabled = true
                 }
             })
         } catch (se: Exception) {
             Log.e("TAG", "Security Exception")
         }
     }


 }