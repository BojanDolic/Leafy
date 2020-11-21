package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.FragmentSelectPlantBinding
import com.electroniccode.leafy.util.Constants

class SelectPlantFragment : Fragment() {

    private var _binding: FragmentSelectPlantBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelectPlantBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cornCard.setOnClickListener {
            findNavController().navigate(SelectPlantFragmentDirections.actionSelectPlantFragmentToLeafyVisionFragment(Constants.PLANT_TYPE_CORN))
        }
        binding.potatoCard.setOnClickListener {
            findNavController().navigate(SelectPlantFragmentDirections.actionSelectPlantFragmentToLeafyVisionFragment(Constants.PLANT_TYPE_POTATO))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}