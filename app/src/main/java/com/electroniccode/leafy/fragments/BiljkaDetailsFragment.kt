package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.BiljkaDetailsFragmentBinding
import com.electroniccode.leafy.databinding.LeafyBookElementBinding
import com.electroniccode.leafy.databinding.LeafyBookPreparatiElementBinding
import com.electroniccode.leafy.models.Biljka


class BiljkaDetailsFragment : Fragment() {

    private var _binding: BiljkaDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: BiljkaDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BiljkaDetailsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.biljka.let {
            createContainers(it)
        }

    }

    fun createContainers(biljka: Biljka) {

        binding.biljkeDetailsTitle.text = biljka.imeBiljke

        Glide.with(requireContext())
            .load(biljka.slikaBiljke)
            .into(binding.biljkaDetailsImage)

        for(i in biljka.naslovi.indices) {

            val container =
                LeafyBookElementBinding.inflate(layoutInflater, binding.biljkeDetailsContainer, false)
            val containerView = container.root

            container.leafyBookElementTitle.text = biljka.naslovi[i]
            container.leafyBookElementDesc.text = biljka.opisi[i]

            container.leafyBookElementImage.visibility = View.GONE

            binding.biljkeDetailsContainer.addView(containerView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}