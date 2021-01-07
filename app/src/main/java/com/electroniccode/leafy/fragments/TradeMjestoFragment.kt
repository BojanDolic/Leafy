package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.SpinnerLoadingItemBinding
import com.electroniccode.leafy.databinding.TradeMjestoFragmentBinding
import com.electroniccode.leafy.models.Mjesto
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class TradeMjestoFragment : Fragment() {

    private var _binding: TradeMjestoFragmentBinding? = null
    private val binding get() = _binding!!

    private val mjestaObjects = mutableListOf<Mjesto?>()

    private val database by lazy { FirebaseFirestore.getInstance() }
    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    private lateinit var dropdown: AutoCompleteTextView
    private val noItemsArray = arrayOf("Dodajte prodajno mjesto")
    private val loadingItemsArray = arrayOf("Učitavanje...")

    private val viewModel: LeafyTradeViewModel by navGraphViewModels(R.id.navigation_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TradeMjestoFragmentBinding.inflate(inflater, container, false)
        dropdown = (binding.leafyTradeProizvodiDropdown.editText as AutoCompleteTextView)
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

        setupDropDownAdapterLoadingElements()

        viewModel.izabranoMjesto.value?.let {
            dropdown.setText(it.imeMjesta, false)
        }

        user?.let {

            GlobalScope.launch {

                try {

                    val mjesta =
                        database.collection("korisnici/${it.uid}/prodajnaMjesta").get().await()

                    if(mjestaObjects.size != 0)
                        mjestaObjects.clear()

                    for (dokument in mjesta.documents) {
                        val mjesto = dokument.toObject(Mjesto::class.java)

                        mjestaObjects.add(mjesto)
                    }

                    withContext(Dispatchers.Main) {
                        if (mjestaObjects.size != 0)
                            setupDropDownAdapterWithElements(mjestaObjects)
                        else setupDropDownAdapterNoElements()
                    }

                } catch (e: Throwable) {
                    Log.e("TradeMjestoFragment", "onViewCreated: ", e)
                }


            }

        }

        viewModel.izabranoMjesto.observe(viewLifecycleOwner, { mjesto ->
            mjesto?.let {
                binding.tradeProdajnoMjestoNextbtn.visibility = View.VISIBLE
            } ?: run { binding.tradeProdajnoMjestoNextbtn.visibility = View.GONE }
        })

        binding.tradeProdajnoMjestoNextbtn.setOnClickListener {
            findNavController().navigate(TradeMjestoFragmentDirections.actionTradeMjestoFragmentToTradeProizvodFragment())
        }

    }

    /**
     * Adapter za dropdown kada korisnik nema kreiranih lokacija
     * Klikom na "Dodaj prodajno mjesto", vodi ga na ekran za dodavanje lokacije
     */

    fun setupDropDownAdapterNoElements() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row_only_text, noItemsArray)
        dropdown.setAdapter(adapter)

        dropdown.setOnItemClickListener { parent, view, position, id ->
            if(position == 0) {
                findNavController().navigate(TradeMjestoFragmentDirections.actionTradeMjestoFragmentToCreateProdajnoMjestoFragment())
            }
        }
    }

    /**
     * Adapter koji prikazuje tekst učitavanja podataka sa servera
     */

    fun setupDropDownAdapterLoadingElements() {

        val adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_row_only_text, loadingItemsArray)
        dropdown.setAdapter(adapter)

    }

    /**
     * Adapter za dropdown koji prikazuje korisnikove lokacije
     * Lokacije se učitavaju direktno sa servera
     *
     * @param mjesta Lista svih lokacija koje su preuzete i pretvorene u Mjesto objekat
     * @see Mjesto
     */

    fun setupDropDownAdapterWithElements(mjesta: List<Mjesto?>) {

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_row_only_text, mjesta)
        dropdown.setAdapter(adapter)


        dropdown.setOnItemClickListener { parent, view, position, id ->
            viewModel.izabranoMjesto.value = mjesta[position]
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}