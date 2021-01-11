package com.electroniccode.leafy.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ArrayAdapterWithIcon
import com.electroniccode.leafy.databinding.LeafyBuyFragmentBinding
import com.electroniccode.leafy.models.Proizvodi
import com.electroniccode.leafy.showErrorSnackbar
import com.electroniccode.leafy.util.Constants
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import id.zelory.compressor.loadBitmap
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.log
import kotlin.math.log10

const val LOCATION_PERM = 250

class LeafyBuyFragment : Fragment() {

    private var _binding: LeafyBuyFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropdown: AutoCompleteTextView

    private var izabranProizvod = ""
    private lateinit var dataHashMap: HashMap<*, *>

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var requestingUpdates = false

    private var lokacija = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LeafyBuyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        fusedLocationProvider =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.buySearchBtn.setOnClickListener {
            checkSettings()
        }

    }

    @AfterPermissionGranted(LOCATION_PERM)
    fun checkSettings() {
        checkLocationSettings()
    }

    fun checkLocationSettings() {

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireContext())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            checkPermissionsAndNavigate()
        }

        task.addOnFailureListener {
            enableGpsSettings()
            Log.e("TAG", "checkLocationSettings: ", it)
        }

    }

    fun checkPermissionsAndNavigate() {
        if(EasyPermissions.hasPermissions(requireContext(), *Constants.locationPerm)) {
            findNavController().navigate(LeafyBuyFragmentDirections.actionLeafyBuyFragmentToBuyFragmentMap())
        } else EasyPermissions.requestPermissions(
            this,
            getString(R.string.lokacija_permission_rationale),
            LOCATION_PERM,
            *Constants.locationPerm
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("requestingLocationUpdatesKey", requestingUpdates)
        super.onSaveInstanceState(outState)
    }

    fun enableGpsSettings() {
        val intent = Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS
        )
        startActivity(intent)
    }

    fun isGpsEnabled(): Boolean =
        ((requireContext().getSystemService(Context.LOCATION_SERVICE)) as LocationManager)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)

    fun getProizvodiFromRadius() {

        if (dataHashMap.isNotEmpty()) {
            val functions = FirebaseFunctions.getInstance("europe-west1")

            functions.getHttpsCallable("getProizvodiFromRadius")
                .call(dataHashMap)
                .continueWith {
                    try {

                        val mapper = jacksonObjectMapper()
                        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)

                        val gson = Gson()
                        val stringJson = gson.toJson(it.result?.data)

                        Log.d("TAG", "getProizvodiFromRadius: $stringJson")

                        val proizvodiObject = mapper.readValue<Proizvodi>(stringJson)


                        //if(((map.get("listaProizvoda")) as ArrayList<String>).size != 0) {
                        /*for(id in (map.get("listaProizvoda")) as ArrayList<String>) {
                            proizvodi.add(id)
                            Log.d("TAG", "\n$id\n\n")
                        }*/

                        if (proizvodiObject.idProizvoda.isNotEmpty()) {



                            /*findNavController().navigate(
                                LeafyBuyFragmentDirections.actionLeafyBuyFragmentToLeafyBuyProizvodiFragment(
                                    proizvodiObject
                                )
                            )*/
                        } else {
                            binding.buySearchBtn.isEnabled = true
                            binding.buySearchBtn.text = resources.getString(R.string.pretrazi_text)

                            Snackbar.make(
                                binding.root,
                                "Nema proizvoda u Vašoj blizini !",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        Log.e("TAG", "onViewCreated: ", e)

                        binding.buySearchBtn.isEnabled = true
                        binding.buySearchBtn.text = resources.getString(R.string.pretrazi_text)

                        showErrorSnackbar(
                            binding.root,
                            "Greška pri preuzimanju podataka !"
                        )
                    }
                }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}