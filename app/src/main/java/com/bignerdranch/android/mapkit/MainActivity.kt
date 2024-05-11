package com.bignerdranch.android.mapkit

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var locationButton: FloatingActionButton
    private lateinit var trafficButton: ToggleButton
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapObjectCollection: MapObjectCollection

    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {
            setMark(p1)
        }
        override fun onMapLongTap(p0: Map, p1: Point) {
            setMark(p1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("f683540d-2c87-4c92-ad37-8604aab1674a")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        locationButton = findViewById(R.id.floatingActionButton)
        trafficButton = findViewById(R.id.toggleButton)
        mapView = findViewById(R.id.mapView)

        locationButton.setOnClickListener {
            toLocation()
        }

        val mapKit = MapKitFactory.getInstance()
        val trafficLayer = mapKit.createTrafficLayer(mapView.mapWindow)

        locationPermission()

        trafficButton.setOnCheckedChangeListener { _, isChecked ->
            trafficLayer.isTrafficVisible = isChecked
        }

        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true

        mapObjectCollection = mapView.map.mapObjects.addCollection()

        mapView.map.addInputListener(inputListener)
    }

    override fun onStop() {
        super.onStop()

        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun locationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }
    }

    private fun toLocation() {
        mapView.map.move(
            CameraPosition(
//                Point(55.354993, 86.085805),
                userLocationLayer.cameraPosition()!!.target,
                18f,
                0f,
                0f
            ),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
        println(userLocationLayer.cameraPosition()!!.target);
    }

    fun setMark(point: Point) {
        mapObjectCollection.addPlacemark(point)
    }
}