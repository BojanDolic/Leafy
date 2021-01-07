package com.electroniccode.leafy.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ArrayAdapterWithIcon
import com.electroniccode.leafy.databinding.ProdajZitariceFragmentBinding

class ProdajZitariceFragment : Fragment() {



    private val biljke = arrayOf("Kukuruz", "Pšenica", "Krompir", "Krastavac", "Jabuka", "Šljiva")

    private var _binding: ProdajZitariceFragmentBinding? = null
    private val binding get() = _binding!!

    //private val args: ProdajZitariceFragmentArgs by navArgs()

    lateinit var spinner: AutoCompleteTextView
    lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProdajZitariceFragmentBinding.inflate(inflater, container, false)
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


        animateBtn()

        binding.leafyTradeStartbtn.setOnClickListener {

            val extras = FragmentNavigatorExtras(
                binding.leafyTradeIcon to "sellCropsImage",
                binding.leafyTradeCard to "sellCropsCard",
                binding.leafyTradeDescText to "sellCropsText"
            )

            findNavController().navigate(ProdajZitariceFragmentDirections.actionProdajZitariceFragmentToTradeMjestoFragment(), extras)
        }


    }

    fun animateBtn() {

        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.leafyTradeStartbtn,
            PropertyValuesHolder.ofFloat("scaleX", 1.05f),
            PropertyValuesHolder.ofFloat("scaleY", 1.05f))

        objectAnimator.apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = FastOutSlowInInterpolator()
            repeatMode = ObjectAnimator.REVERSE
        }.start()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}