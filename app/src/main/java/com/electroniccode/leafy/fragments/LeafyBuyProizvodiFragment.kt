package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.LeafyBuyProizvodiAdapter
import com.electroniccode.leafy.databinding.LeafyBuyProizvodiFragmentBinding
import com.electroniccode.leafy.getDocuments
import com.electroniccode.leafy.models.Proizvod
import com.electroniccode.leafy.models.Proizvodi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class LeafyBuyProizvodiFragment : Fragment(), LeafyBuyProizvodiAdapter.OnProizvodClickListener {

    private var _binding: LeafyBuyProizvodiFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: LeafyBuyProizvodiFragmentArgs by navArgs()

    private var proizvodi: List<Proizvod?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LeafyBuyProizvodiFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {

            try {

                proizvodi = FirebaseFirestore.getInstance().getDocuments<Proizvod>(args.proizvodiData.idProizvoda.toList(), "proizvodi")

                for(proizvod in proizvodi.listIterator()) {
                    proizvod?.udaljenost = args.proizvodiData.udaljenosti[proizvodi.indexOf(proizvod)]
                }

                withContext(Dispatchers.Main) {
                    setupRecyclerAdapter()
                }

            } catch (e: Exception) {
                Log.e("TAG", "onViewCreated: ", e)
            }


        }



    }

    fun setupRecyclerAdapter() {

        val adapter = LeafyBuyProizvodiAdapter(proizvodi)
        adapter.setProizvodClickListener(this)

        binding.proizvodiRecycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun onProizvodClicked(proizvod: Proizvod?) {
        findNavController().navigate(LeafyBuyProizvodiFragmentDirections.actionLeafyBuyProizvodiFragmentToBuyProizvodDetailsFragment(proizvod))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}