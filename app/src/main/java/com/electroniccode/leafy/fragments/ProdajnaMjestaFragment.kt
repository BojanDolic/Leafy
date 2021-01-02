package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.FragmentProdajnaMjestaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Služi za izlistavanje svih prodajnih mjesta koje je korisnik dodao
 */

class ProdajnaMjestaFragment : Fragment() {

    private var _binding: FragmentProdajnaMjestaBinding? = null
    private val binding get() = _binding!!

    private val user by lazy { FirebaseAuth.getInstance().currentUser }
    private val database by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProdajnaMjestaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user?.let {
            val query = database.collection("korisnici/${it.uid}/prodajnaMjesta")



        }

        binding.prodajnoMjestoAddBtn.setOnClickListener {
            findNavController().navigate(ProdajnaMjestaFragmentDirections.actionProdajnaMjestaFragmentToCreateProdajnoMjestoFragment())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}