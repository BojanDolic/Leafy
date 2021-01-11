package com.electroniccode.leafy.fragments

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.TradeSlikaFragmentBinding
import com.electroniccode.leafy.util.Constants
import com.electroniccode.leafy.util.UtilFunctions
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel


class TradeSlikaFragment : Fragment() {

    private var _binding: TradeSlikaFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by navGraphViewModels<LeafyTradeViewModel>(R.id.create_proizvod_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TradeSlikaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        viewModel.slikaProizvodaUri.value?.let {
            binding.tradeSlikaAddPlaceholderImg.visibility = View.GONE
            binding.tradeSlikaProizvoda.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(it)
                .into(binding.tradeSlikaProizvoda)
        }

        viewModel.slikaProizvodaUri.observe(viewLifecycleOwner, { slikaUri ->
            slikaUri?.let {
                binding.tradeNextbtn.visibility = View.VISIBLE
            }
        })

        binding.tradeSlikaSelectCard.setOnClickListener {
            UtilFunctions.pickGalleryImage(this, Constants.PICK_GALLERY_IMAGE_RQ)
        }

        binding.tradeNextbtn.setOnClickListener {
            findNavController().navigate(TradeSlikaFragmentDirections.actionTradeSlikaFragmentToTradeOpisFragment())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.PICK_GALLERY_IMAGE_RQ) {
            data?.let {
                viewModel.slikaProizvodaUri.value = it.data

                binding.tradeSlikaAddPlaceholderImg.visibility = View.GONE
                binding.tradeSlikaProizvoda.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(it.data)
                    .into(binding.tradeSlikaProizvoda)

            }
        }

    }

}