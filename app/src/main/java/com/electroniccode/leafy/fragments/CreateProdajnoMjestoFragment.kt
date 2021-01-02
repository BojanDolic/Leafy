package com.electroniccode.leafy.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.CreateProdajnoMjestoFragmentBinding
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Annotation
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.ImageSource
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception

class CreateProdajnoMjestoFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = CreateProdajnoMjestoFragment()
    }

    private val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private var _binding: CreateProdajnoMjestoFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateProdajnoMjestoViewModel

    private lateinit var map: MapboxMap
    private lateinit var locationEngine: LocationEngine

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(requireContext(), resources.getString(R.string.mapbox_access_token))
        _binding = CreateProdajnoMjestoFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(CreateProdajnoMjestoViewModel::class.java)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createStoreMap.onCreate(savedInstanceState)

        binding.createStoreMap.getMapAsync(this)



    }

    fun addMarker(latLng: LatLng) {

        val symbolManager = SymbolManager(binding.createStoreMap, map, map.style!!)

        val icon = BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)
        map.style?.addImage("marker", icon)

        val symbol = symbolManager.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage("marker")
                .withIconSize(2f)
        )

        //map.style?.addLayer()

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.SATELLITE, Style.OnStyleLoaded {
            enableLocation(it)

            map.addOnMapClickListener {
                addMarker(it)
                true
            }

        })


    }

    @SuppressLint("MissingPermission")
    fun enableLocation(style: Style) {

        if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
            initializeLocationEngine()

            val locationComponent = map.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions
                    .builder(requireContext(), style).build()
            )

            locationComponent.isLocationComponentEnabled = true



        } else EasyPermissions.requestPermissions(
            this@CreateProdajnoMjestoFragment,
            "Rationale",
            20,
            *perms
        )
    }

    @SuppressLint("MissingPermission")
    fun initializeLocationEngine() {

        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())

        locationEngine.getLastLocation(object : LocationEngineCallback<LocationEngineResult> {

            override fun onSuccess(result: LocationEngineResult?) {

                val location = result?.lastLocation

                location?.let { loc ->
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                loc.latitude,
                                loc.longitude
                            ), 13.0
                        )
                    )
                }
            }

            override fun onFailure(exception: Exception) {

            }
        })


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onResume() {
        super.onResume()
        binding.createStoreMap.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.createStoreMap.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}