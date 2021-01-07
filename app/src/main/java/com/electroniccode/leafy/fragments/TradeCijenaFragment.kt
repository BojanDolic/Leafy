package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.TradeCijenaFragmentBinding
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel

class TradeCijenaFragment : Fragment() {

    private var _binding: TradeCijenaFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by navGraphViewModels<LeafyTradeViewModel>(R.id.navigation_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TradeCijenaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        checkForCijenaValue()

        binding.tradeCijenaInput.addTextChangedListener {
            it?.let {
                if(it.isNotEmpty())
                    viewModel.setCijena(it.toString().toFloat())

                if(viewModel.isCijenaValid()) {
                    binding.tradeCijenaInputLayout.error = null
                    binding.tradeCijenaInputLayout.isErrorEnabled = false
                    binding.tradeNextbtn.visibility = View.VISIBLE
                } else {
                    binding.tradeNextbtn.visibility = View.GONE
                    binding.tradeCijenaInputLayout.error = "Cijena mora biti veća od 0.0\u20AC"
                }
            }

        }

    }

    fun checkForCijenaValue() {
        if(viewModel.isCijenaValid())
            binding.tradeNextbtn.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}