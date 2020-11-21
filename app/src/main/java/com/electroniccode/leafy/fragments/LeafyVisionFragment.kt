package com.electroniccode.leafy.fragments

import android.animation.TimeInterpolator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Interpolator
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.LeafyVisionFragmentBinding
import com.electroniccode.leafy.models.Bolest
import com.electroniccode.leafy.util.Constants
import com.electroniccode.leafy.util.UtilFunctions
import com.electroniccode.leafy.viewmodels.LeafyVisionViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class LeafyVisionFragment : Fragment() {

    companion object {
        fun newInstance() = LeafyVisionFragment()
    }

    private var plantType: Int = 0
    val args: LeafyVisionFragmentArgs by navArgs()

    private lateinit var viewModel: LeafyVisionViewModel

    private var _binding: LeafyVisionFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>

    private var bolest: Bolest? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LeafyVisionFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LeafyVisionViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantType = args.plantType

        if (plantType == 0)
            findNavController().navigateUp()

        if(viewModel.imageUri != null && viewModel.bolest != null) {

            binding.visionImageview.imageTintList = null

            binding.visionIconImagePerm.visibility = View.GONE
            binding.skenirajBiljkuBtn.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(viewModel.imageUri)
                .into(binding.visionImageview)

            binding.bottomSheetInclude.leafyVisionBottomSheetTitle.text =
                viewModel.bolest?.imeBolesti
            binding.bottomSheetInclude.leafyVisionBottomSheetDesc.text =
                viewModel.bolest!!.opis?.get(0)

            Glide.with(requireContext())
                .load(viewModel.bolest?.slikaBolesti)
                .into(binding.bottomSheetInclude.leafyVisionBottomSheetImage)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


        } else {
            binding.skenirajBiljkuBtn.visibility = View.GONE
        }



        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheetInclude.leafyVisionBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        binding.skenirajBiljkuBtn.setOnClickListener {
            skenirajBiljku()
        }

        binding.bottomSheetInclude.leafyVisionBottomSheetProcitajBtn.setOnClickListener {
            findNavController().navigate(LeafyVisionFragmentDirections.actionLeafyVisionFragmentToBookViewerFragment(bolest, Constants.TIP_BOLEST))
        }


        binding.visionImageview.setOnClickListener {
            //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode != Activity.RESULT_CANCELED && data != null) {

                viewModel.imageUri = data.data

                binding.visionIconImagePerm.visibility = View.GONE
                binding.skenirajBiljkuBtn.visibility = View.VISIBLE

                binding.visionImageview.imageTintList = null
                Glide.with(requireContext())
                    .load(viewModel.imageUri)
                    .into(binding.visionImageview)


            }

        }

    }

    fun skenirajBiljku() {

        if (viewModel.imageUri != null) {

            viewModel.labelImageUsingML(viewModel.imageUri!!, plantType).addOnCompleteListener {


                if (it.isSuccessful) {

                    val bolestName = UtilFunctions.parseBolestName(it.result?.get(0)?.text!!)

                    Log.d("TAG", "skenirajBiljku: $bolestName \n\n ${it.result?.get(0).toString()}\n${it.result?.get(0)?.confidence.toString()}")

                    if(TextUtils.equals(bolestName, "Zdrav")) {

                    } else {

                        viewModel.getBolestInfoFromDatabase(bolestName).addOnSuccessListener {

                            if (it != null) {

                                bolest = it.toObject(Bolest::class.java)

                                viewModel.bolest = bolest

                                binding.bottomSheetInclude.leafyVisionBottomSheetTitle.text =
                                    viewModel.bolest?.imeBolesti
                                binding.bottomSheetInclude.leafyVisionBottomSheetDesc.text =
                                    viewModel.bolest?.opis?.get(0)

                                Glide.with(requireContext())
                                    .load(viewModel.bolest?.slikaBolesti)
                                    .into(binding.bottomSheetInclude.leafyVisionBottomSheetImage)

                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                                //Log.d("TAG", "skenirajBiljku: " + bolest.toString())

                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Greška pri preuzimanju dokumenta",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }



                    ////Toast.makeText(requireContext(), it.result?.get(0)?.text + it.result?.get(0)?.confidence, Toast.LENGTH_LONG).show()

                } else Snackbar.make(binding.root, "Greška", Snackbar.LENGTH_SHORT).apply {
                    setBackgroundTint(resources.getColor(R.color.errorRed))
                }.show()

            }

        } else Toast.makeText(requireContext(), "Izaberite sliku", Toast.LENGTH_SHORT).show()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}