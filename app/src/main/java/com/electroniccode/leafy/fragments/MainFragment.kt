package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.MainFragmentBinding
import com.electroniccode.leafy.viewmodels.MainFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainFragmentViewModel

    private var _binding: MainFragmentBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.forumCard.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToForumFragment())
        }

        binding.profilCard.setOnClickListener {
            //FirebaseAuth.getInstance().signOut()
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToProfilFragment())
        }

        binding.scanCard.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToSelectPlantFragment())
        }

        binding.prodajZitariceCard.setOnClickListener {

            val extras = FragmentNavigator.Extras.Builder()
            extras.addSharedElement(binding.prodajZitariceIcon, "sellCropsImage")
            extras.addSharedElement(binding.prodajZitariceCard, "sellCropsCard")
            extras.addSharedElement(binding.prodajZitariceDescription, "sellCropsText")

            findNavController().navigate(MainFragmentDirections.actionMainFragmentToProdajZitariceFragment(), extras.build())

        }

        binding.kupiZitariceCard.setOnClickListener {

            findNavController().navigate(MainFragmentDirections.actionMainFragmentToLeafyBuyFragment())

            /*val functions = FirebaseFunctions.getInstance("europe-west1")

            val data = hashMapOf(
                "lat" to 44.733205,
                "lng" to 18.085295
            )

            functions.getHttpsCallable("getProizvodiFromRadius")
                .call(data)
                .continueWith {
                    try {

                        Log.d("TAG", "Preuzimanje sa firebasea: ${it.result?.data.toString()}")

                    } catch (e: Exception) {
                        Log.e("TAG", "onViewCreated: ", e)
                    }
                }*/
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}