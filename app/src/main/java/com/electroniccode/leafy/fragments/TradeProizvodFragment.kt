package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ArrayAdapterWithIcon
import com.electroniccode.leafy.databinding.TradeProizvodFragmentBinding
import com.electroniccode.leafy.util.Constants
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel


class TradeProizvodFragment : Fragment() {

    private var _binding: TradeProizvodFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var dropdown: AutoCompleteTextView

    private val viewModel: LeafyTradeViewModel by navGraphViewModels(R.id.navigation_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TradeProizvodFragmentBinding.inflate(inflater, container, false)
        dropdown = (binding.tradeProizvodiDropdown.editText as AutoCompleteTextView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        setupDropDownWithProizvodi()

        viewModel.izabranProizvod.observe(viewLifecycleOwner, { proizvod ->
            if(!TextUtils.equals(proizvod, ""))
                binding.tradeNextbtn.visibility = View.VISIBLE
        })

        binding.tradeNextbtn.setOnClickListener {
            findNavController().navigate(TradeProizvodFragmentDirections.actionTradeProizvodFragmentToTradeSlikaFragment())
        }

    }

    fun setupDropDownWithProizvodi() {
        val adapter = ArrayAdapterWithIcon(
            requireContext(),
            R.layout.spinner_row, Constants.proizvodi)

        dropdown.setAdapter(adapter)
        dropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.izabranProizvod.value = Constants.proizvodi[position]
        }

        if(viewModel.izabranProizvod.value?.isNotEmpty()!!)
            dropdown.setText(viewModel.izabranProizvod.value, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}