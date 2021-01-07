package com.electroniccode.leafy.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.CreateProdajnoMjestoFragmentBinding
import com.electroniccode.leafy.databinding.EdittextViewBinding
import com.electroniccode.leafy.models.Mjesto
import com.electroniccode.leafy.viewmodels.CreateProdajnoMjestoViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pub.devrel.easypermissions.EasyPermissions

class CreateProdajnoMjestoFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private var _binding: CreateProdajnoMjestoFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateProdajnoMjestoViewModel

    private val user by lazy { FirebaseAuth.getInstance().currentUser }
    private val database by lazy { FirebaseFirestore.getInstance() }

    //private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = CreateProdajnoMjestoFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(CreateProdajnoMjestoViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.createProdajnoMjestoDaljeBtn.setOnClickListener {
            openTitleDialog()
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.setOnMapClickListener(this)
        getUserLocation()

    }

    override fun onMapClick(p0: LatLng?) {
        addMjestoMarker(p0)
    }

    fun addMjestoMarker(latLng: LatLng?) {

        map?.clear()

        val markerOptions = MarkerOptions()
            .draggable(false)
            .title("Prodajno mjesto")
            .position(latLng!!)

        marker = map?.addMarker(markerOptions)
        binding.createProdajnoMjestoDaljeBtn.isEnabled = true

    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        try {
            if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
                map?.isMyLocationEnabled = true
                val lastLocation = fusedLocationProvider.lastLocation

                lastLocation.addOnCompleteListener {
                    if (it.isSuccessful) {

                        val location = it.result

                        location?.let {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    15f
                                )
                            )
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Nije moguće locirati uređaj !",
                            Toast.LENGTH_LONG
                        ).show()
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(44.246755819754, 17.765397193562656), // Default na BiH
                                5f
                            )
                        )
                    }
                }


            } else EasyPermissions.requestPermissions(
                this@CreateProdajnoMjestoFragment,
                "Rationale",
                20,
                *perms
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }


    }

    fun openTitleDialog() {

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        val inflater = layoutInflater

        val inflatedView = EdittextViewBinding.inflate(inflater, binding.root, false)

        alertDialogBuilder.setView(inflatedView.root)
            .setTitle("Ime prodajnog mjesta")
            .setMessage("Unesite ime vašeg prodajnog mjesta (Kuća npr.). Ime neće biti vidljivo drugim korisnicima !")
            .setPositiveButton("Dodaj mjesto", DialogInterface.OnClickListener { dialog, which ->

                val imeProdajnogMjesta = inflatedView.dialogEdittext.text.toString()

                if(imeProdajnogMjesta.isEmpty() || (imeProdajnogMjesta.length > 15 && imeProdajnogMjesta.length < 4))
                    inflatedView.dialogEdittext.error = "Ime mjesta mora biti manje od 15 karaktera i veće od 3 karaktera"
                else {
                    val mjesto = Mjesto(
                        imeProdajnogMjesta,
                        marker?.position?.latitude!!,
                        marker?.position?.longitude!!)

                    insertMjestoIntoDatabase(mjesto)
                }

            })
            .setNegativeButton("Poništi", DialogInterface.OnClickListener { dialog, which ->  })

        alertDialogBuilder.show()

    }

    fun insertMjestoIntoDatabase(mjesto: Mjesto) {
        binding.createProdajnoMjestoDaljeBtn.isEnabled = false
        binding.createProdajnoMjestoDaljeBtn.text = resources.getText(R.string.cuvanje_text)

        database.collection("korisnici/${user?.uid}/prodajnaMjesta").add(mjesto).addOnCompleteListener {
            if(it.isSuccessful) {
                Snackbar.make(binding.root, "Mjesto sačuvano !", Snackbar.LENGTH_SHORT).show()
                object : CountDownTimer(1500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }

                    override fun onFinish() {
                        findNavController().navigateUp()
                    }
                }.start()


            }

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}