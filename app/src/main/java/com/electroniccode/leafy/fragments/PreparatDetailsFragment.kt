package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.*
import com.electroniccode.leafy.models.Preparat
import com.electroniccode.leafy.models.Usjev
import com.google.firebase.firestore.FirebaseFirestore
import java.util.jar.Attributes

class PreparatDetailsFragment : Fragment() {

    private var _binding: PreparatDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: PreparatDetailsFragmentArgs by navArgs()

    private lateinit var viewModel: PreparatDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PreparatDetailsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PreparatDetailsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.preparat?.let {
            createPreparatDesign(it)

            Glide.with(requireContext())
                .load(it.slika)
                .into(binding.preparatDetailsPreparatImage)

            binding.preparatDetailsPreparatTitle.text = it.imePreparata

        }


    }

    fun createPreparatDesign(preparat: Preparat) {

        if(preparat.usjevi.isNotEmpty())
            createPrimjenaData(preparat)
        if(preparat.naslovi.isNotEmpty())
            createOpisPreparata(preparat)

    }

    fun createOpisPreparata(preparat: Preparat) {
        for(i in preparat.naslovi.indices) {

            val opisItem = LeafyBookElementBinding.inflate(layoutInflater)
            val opisView = opisItem.root

            opisItem.leafyBookElementTitle.text = preparat.naslovi[i]
            opisItem.leafyBookElementDesc.text = preparat.opisi[i]
            opisItem.leafyBookElementImage.visibility = View.GONE

            binding.preparatDetailsContainer.addView(opisView)
        }
    }

    fun createPrimjenaData(preparat: Preparat) {

        // Prolaz kroz sve elemente (usjeve)
        for(i in preparat.usjevi.indices) {

            // Inflateovanje potrebnog carda za prikaz informacija
            val primjenaCard = PreparatPrimjenaCardBinding.inflate(layoutInflater, binding.preparatDetailsContainer, false)
            val primjenaView = primjenaCard.root

            primjenaCard.primjenaBiljkaTitle.text = preparat.usjevi[i].imeUsjeva

            primjenaView.setOnClickListener {
                findNavController().navigate(PreparatDetailsFragmentDirections.actionPreparatDetailsFragmentToTretiranjeUsjevaDetails(preparat.usjevi[i]))
            }

            binding.preparatDetailsContainer.addView(primjenaView)

        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}