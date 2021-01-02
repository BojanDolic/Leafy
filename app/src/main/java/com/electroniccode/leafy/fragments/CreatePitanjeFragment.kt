package com.electroniccode.leafy.fragments

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.CreatePitanjeFragmentBinding
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.viewmodels.CreatePitanjeViewModel
import com.google.android.gms.common.util.IOUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.FileUtil
import com.google.firebase.storage.FirebaseStorage
import com.google.type.Date
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Time
import java.util.*

const val PICK_IMAGE_RQ = 1

class CreatePitanjeFragment : Fragment() {

    private lateinit var viewModel: CreatePitanjeViewModel

    val database = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val user by lazy { FirebaseAuth.getInstance().currentUser }

    private var _binding: CreatePitanjeFragmentBinding? = null
    val binding get() = _binding!!

    var inputStream: InputStream? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreatePitanjeFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CreatePitanjeViewModel::class.java)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.obradaPitanja.observe(viewLifecycleOwner, Observer {

            if (it) {
                binding.pitanjeOpis.isEnabled = false
                binding.pitanjeTekst.isEnabled = false
                binding.postaviPitanjeFab.isEnabled = false
            } else {
                binding.pitanjeOpis.isEnabled = true
                binding.pitanjeTekst.isEnabled = true
                binding.postaviPitanjeFab.isEnabled = true
            }

        })



        binding.pitanjeCardImage.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, PICK_IMAGE_RQ)
        }


        binding.postaviPitanjeFab.setOnClickListener {

            val pitanjeText = binding.pitanjeTekst.text.toString().trim()
            val opisText = binding.pitanjeOpis.text.toString().trim()

            if (pitanjeText.isNotEmpty() && opisText.isNotEmpty()) {

                viewModel.obradaPitanja.value = true

                if (viewModel.slikaUri == null) {

                    val pitanje = Pitanje(tekstPitanja =  pitanjeText, opis = opisText, datum = Timestamp.now(), idAutora = user?.uid!!)

                    database.collection("pitanja").document().set(pitanje)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {

                                Snackbar.make(
                                    binding.root,
                                    "Pitanje postavljeno.",
                                    Snackbar.LENGTH_SHORT
                                ).apply {
                                    setBackgroundTint(resources.getColor(R.color.darkGreen))
                                }.show()

                                object : CountDownTimer(2000, 1000) {

                                    override fun onTick(millisUntilFinished: Long) {
                                    }

                                    override fun onFinish() {
                                        findNavController().navigateUp()
                                    }
                                }.start()

                            } else viewModel.obradaPitanja.value = false


                        }

                } else {

                    viewModel.obradaPitanja.value = true

                    var compressedImage: File? = null
                    var tempimageFile: File = File.createTempFile("prefix", "suffix")
                    tempimageFile.deleteOnExit()

                    val outputStream = FileOutputStream(tempimageFile)
                    org.apache.commons.io.IOUtils.copy(inputStream, outputStream)


                    runBlocking {
                        compressedImage = Compressor.compress(
                            requireContext(),
                            tempimageFile
                        )
                    }

                    val storageRef = storage.reference.child("slikePitanja/${UUID.randomUUID()}")

                    val imageUri = Uri.fromFile(compressedImage)

                    if(imageUri != null) {

                        storageRef.putFile(imageUri).addOnCompleteListener {
                            if (it.isSuccessful) {
                                storageRef.downloadUrl.addOnCompleteListener {
                                    if(it.isSuccessful) {

                                        val pitanje = Pitanje(
                                            tekstPitanja =  pitanjeText,
                                            opis = opisText,
                                            slikaPitanja =  it.result.toString(),
                                            datum = Timestamp.now(),
                                            idAutora = user?.uid!!)

                                        database.collection("pitanja").document().set(pitanje)
                                            .addOnCompleteListener {

                                                if (it.isSuccessful) {

                                                    Snackbar.make(
                                                        binding.root,
                                                        "Pitanje postavljeno.",
                                                        Snackbar.LENGTH_SHORT
                                                    ).apply {
                                                        setBackgroundTint(resources.getColor(R.color.darkGreen))
                                                    }.show()

                                                    object : CountDownTimer(2000, 1000) {

                                                        override fun onTick(millisUntilFinished: Long) {
                                                        }

                                                        override fun onFinish() {
                                                            findNavController().navigateUp()
                                                        }
                                                    }.start()

                                                } else viewModel.obradaPitanja.value = false


                                            }


                                    } else viewModel.obradaPitanja.value = false

                                }

                            } else viewModel.obradaPitanja.value = false


                        }

                   }



                }

            } else Snackbar.make(
                binding.root,
                "Sva polja moraju biti popunjena.",
                Snackbar.LENGTH_LONG
            ).apply {
                setBackgroundTint(resources.getColor(R.color.errorRed))
            }.show()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_RQ && data?.data != null) {

            viewModel.slikaUri = data.data

            inputStream = requireContext().contentResolver.openInputStream(viewModel.slikaUri!!)

            binding.pitanjeImage.imageTintList = null
            Glide.with(requireContext())
                .load(data.data)
                .into(binding.pitanjeImage)

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}