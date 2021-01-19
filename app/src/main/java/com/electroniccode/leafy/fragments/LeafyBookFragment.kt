package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.LeafyBookFragmentBinding
import com.electroniccode.leafy.util.Constants

class LeafyBookFragment : Fragment() {

    private var _binding: LeafyBookFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LeafyBookFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookBiljkeCard.setOnClickListener {
            findNavController().navigate(
                LeafyBookFragmentDirections.actionLeafyBookFragmentToLeafyBookListFragment(
                    Constants.KATEGORIJA_BILJKE))
        }
        binding.bookBolestiCard.setOnClickListener {
            findNavController().navigate(
                LeafyBookFragmentDirections.actionLeafyBookFragmentToLeafyBookListFragment(
                    Constants.KATEGORIJA_BOLESTI))
        }
        binding.bookHerbicidiCard.setOnClickListener {
            findNavController().navigate(
                LeafyBookFragmentDirections.actionLeafyBookFragmentToLeafyBookListFragment(
                    Constants.KATEGORIJA_HERBICID))
        }
        binding.bookFungicidiCard.setOnClickListener {
            findNavController().navigate(
                LeafyBookFragmentDirections.actionLeafyBookFragmentToLeafyBookListFragment(
                    Constants.KATEGORIJA_FUNGICIDI))
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}