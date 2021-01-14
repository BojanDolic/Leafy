package com.electroniccode.leafy.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ArrayAdapterWithIcon
import com.electroniccode.leafy.databinding.MapBuyFragmentBinding
import com.electroniccode.leafy.models.Proizvodi
import com.electroniccode.leafy.showErrorSnackbar
import com.electroniccode.leafy.util.Constants
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson

/**
 * Fragment koji prikazuje trenutnu lokaciju korisnika na mapi
 * Izborom proizvoda i klikom na dugme "Pretraži"
 */

class BuyFragmentMap : Fragment(), OnMapReadyCallback {

    private var _binding: MapBuyFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private lateinit var dropdown: AutoCompleteTextView
    private var izabranProizvod = ""

    private var dataHashMap: MutableLiveData<HashMap<*, *>> = MutableLiveData()
    private val isSearching: MutableLiveData<Boolean> = MutableLiveData(false)

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapBuyFragmentBinding.inflate(inflater, container, false)
        dropdown = (binding.tradeProizvodiDropdown.editText) as AutoCompleteTextView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropDownProizvoda()

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        dataHashMap.observe(viewLifecycleOwner, { hashmap ->

            if (hashmap.isNotEmpty() && izabranProizvod.isNotEmpty() && !isSearching.value!!)
                binding.buySearchBtn.isEnabled = true

        })

        binding.buySearchBtn.setOnClickListener {

            dataHashMap.value?.let { hashmap ->
                if (hashmap.isNotEmpty() && izabranProizvod.isNotEmpty()) {
                    isSearching.value = true
                    getProizvodiFromRadius()
                } else {
                    showErrorSnackbar(
                        binding.root,
                        "Greška !"
                    )
                }
            } ?: run {
                showErrorSnackbar(
                    binding.root,
                    "Greška pri određivanju lokacije !"
                )
            }

        }
        
        observeSearchingBool()


    }

    /**
     * U zavisnosti od vrijednosti booleana, postavlja UI elemente na enabled/disabled
     */
    fun observeSearchingBool() {
        
        isSearching.observe(viewLifecycleOwner, { searching ->

            if(searching) {
                dropdown.isEnabled = false

                binding.buySearchBtn.isEnabled = false
                binding.buySearchBtn.text = getText(R.string.pretrazivanje_text)

            } else {
                dropdown.isEnabled = true

                binding.buySearchBtn.isEnabled = true
                binding.buySearchBtn.text = getText(R.string.pretrazi_text)
            }
        })
        
    }
    
    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        p0 ?: return
        map = p0

        map.uiSettings.isMyLocationButtonEnabled = false
        map.isMyLocationEnabled = true

        fusedLocationProvider.lastLocation.addOnCompleteListener {

            if (it.isSuccessful) {

                it.result?.let { location ->

                    dataHashMap.value = hashMapOf(
                        "lat" to location.latitude,
                        "lng" to location.longitude,
                        "vrsta" to izabranProizvod
                    )

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            11f
                        )
                    )

                    drawCircle(LatLng(location.latitude, location.longitude))

                }

            }
        }

        createLocationRequest()
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                p0 ?: return
                for(location in p0.locations) {
                    dataHashMap.value = hashMapOf(
                        "lat" to location.latitude,
                        "lng" to location.longitude,
                        "vrsta" to izabranProizvod
                    )

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            11f
                        )
                    )

                    drawCircle(LatLng(location.latitude, location.longitude))
                }
            }
        }
        fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    fun drawCircle(latLng: LatLng) {

        map.clear()

        val optionsCircle = CircleOptions()
            .center(latLng)
            .radius(10000.0)
            .fillColor(resources.getColor(R.color.circleColor, null))
            .strokeColor(resources.getColor(R.color.mainColor, null))
            .strokeWidth(5f)

        map.addCircle(optionsCircle)
    }


    fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = 4000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun getProizvodiFromRadius() {

        if (dataHashMap.value?.isNotEmpty()!!) {
            val functions = FirebaseFunctions.getInstance("europe-west1")

            functions.getHttpsCallable("getProizvodiFromRadius")
                .call(
                    hashMapOf(
                        "lat" to dataHashMap.value?.get("lat"),
                        "lng" to dataHashMap.value?.get("lng"),
                        "vrsta" to izabranProizvod
                    )
                )
                .continueWith {
                    try {

                        val proizvodiObject = convertToProizvodiObject(it)

                        // Ako postoje proizvodi, navigira na listu proizvoda
                        if (proizvodiObject.idProizvoda.isNotEmpty()) {

                            isSearching.value = false

                            findNavController().navigate(
                                BuyFragmentMapDirections.actionBuyFragmentMapToLeafyBuyProizvodiFragment(
                                    proizvodiObject
                                )
                            )
                        } else {

                            isSearching.value = false

                            /*binding.buySearchBtn.isEnabled = true
                            binding.buySearchBtn.text = resources.getString(R.string.pretrazi_text)*/

                            Snackbar.make(
                                binding.root,
                                "Nema proizvoda u Vašoj blizini !",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        Log.e("TAG", "onViewCreated: ", e)

                        isSearching.value = false

                        showErrorSnackbar(
                            binding.root,
                            "Greška pri preuzimanju podataka !"
                        )
                    }
                }
        } else isSearching.value = false

    }

    fun convertToProizvodiObject(task: Task<HttpsCallableResult>): Proizvodi {
        val mapper = jacksonObjectMapper()
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)

        val gson = Gson()
        val stringJson = gson.toJson(task.result?.data)

        Log.d("TAG", "getProizvodiFromRadius: $stringJson")

        val proizvodiObject = mapper.readValue<Proizvodi>(stringJson)

        return proizvodiObject
    }

    fun setupDropDownProizvoda() {

        val adapter = ArrayAdapterWithIcon(
            requireContext(),
            R.layout.spinner_row,
            Constants.proizvodi
        )

        dropdown.setAdapter(adapter)

        if (izabranProizvod.isNotEmpty()) {
            dropdown.setText(izabranProizvod)
            binding.buySearchBtn.isEnabled = true
        }


        dropdown.setOnItemClickListener { parent, view, position, id ->
            izabranProizvod = adapter.getItem(position).toString()
            binding.buySearchBtn.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}