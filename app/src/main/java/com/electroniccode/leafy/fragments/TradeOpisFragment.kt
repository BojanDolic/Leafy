package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.TradeOpisFragmentBinding
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel

class TradeOpisFragment : Fragment() {

    private var _binding: TradeOpisFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by navGraphViewModels<LeafyTradeViewModel>(R.id.navigation_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TradeOpisFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        if(viewModel.isNaslovProizvodaValid() && viewModel.isOpisProizvodaValid())
            binding.tradeNextbtn.visibility = View.VISIBLE

        setupEditTextData()

        binding.tradeNaslovInput.addTextChangedListener {
            viewModel.naslovProizvoda = it.toString()

            if(!viewModel.isNaslovProizvodaValid()) {
                binding.tradeNaslovInputLayout.error =
                    "Tekst mora biti duži od 10 karaktera i ne smije sadržati specijalne znakove !"

            }
            else {
                binding.tradeNaslovInputLayout.error = null
                binding.tradeNaslovInputLayout.isErrorEnabled = false
            }

            if(viewModel.isNaslovProizvodaValid() && viewModel.isOpisProizvodaValid())
                binding.tradeNextbtn.visibility = View.VISIBLE
            else binding.tradeNextbtn.visibility = View.GONE
        }

        binding.tradeOpisInput.addTextChangedListener {
            viewModel.opisProizvoda = it.toString()

            if(!viewModel.isOpisProizvodaValid())
                binding.tradeOpisInputLayout.error =
                    "Tekst mora biti duži od 50 karaktera i ne smije sadržati specijalne znakove !"
            else {
                binding.tradeOpisInputLayout.error = null
                binding.tradeOpisInputLayout.isErrorEnabled = false
            }

            if(viewModel.isNaslovProizvodaValid() && viewModel.isOpisProizvodaValid())
                binding.tradeNextbtn.visibility = View.VISIBLE
            else binding.tradeNextbtn.visibility = View.GONE

        }

        binding.tradeNextbtn.setOnClickListener {
            findNavController().navigate(TradeOpisFragmentDirections.actionTradeOpisFragmentToTradeCijenaFragment())
        }

    }

    /**
     * Postavlja vrijednosti edittextova ako su prethodno unijete
     */
    fun setupEditTextData() {
        if(viewModel.naslovProizvoda.isNotEmpty())
            binding.tradeNaslovInput.setText(viewModel.naslovProizvoda)
        if(viewModel.opisProizvoda.isNotEmpty())
            binding.tradeOpisInput.setText(viewModel.opisProizvoda)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}