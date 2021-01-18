package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.LeafyBookPreparatAdapter
import com.electroniccode.leafy.databinding.BookViewerFragmentBinding
import com.electroniccode.leafy.databinding.LeafyBookElementBinding
import com.electroniccode.leafy.databinding.LeafyBookPreparatiElementBinding
import com.electroniccode.leafy.getDocuments
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Preparat
import com.electroniccode.leafy.viewmodels.BookViewerViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.lang.StringBuilder

class BookViewerFragment
    : Fragment(), OnAdapterItemClickedListener {

    private lateinit var viewModel: BookViewerViewModel

    private var _binding: BookViewerFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: BookViewerFragmentArgs by navArgs()

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BookViewerFragmentBinding.inflate(inflater, container, false)

        //requireActivity().setActionBar(binding.bookViewerToolbar)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookViewerCollapsingtoolbar.setCollapsedTitleTextColor(resources.getColor(R.color.black))

        if(args.bolest != null) {

            val bolest = args.bolest

            binding.bookViewerCollapsingtoolbar.title = bolest?.imeBolesti
            Glide.with(requireContext())
                .load(bolest?.slikaBolesti)
                .into(binding.bookViewerImage)

            for (i in bolest?.naslovi!!.indices) {

                val bookElement =
                    LeafyBookElementBinding.inflate(layoutInflater, binding.bookScrollContainer, false)

                bookElement.leafyBookElementTitle.text = bolest.naslovi[i]

                val stringBuilder = StringBuilder("")
                val stringovi: List<String> = bolest.opis?.get(i)?.split("\\n")!!

                if(stringovi.size > 0) {
                    for (string in stringovi) {
                        stringBuilder.append("\n" + string.trim())
                        bookElement.leafyBookElementDesc.text = stringBuilder
                    }
                } else {
                    bookElement.leafyBookElementDesc.text = bolest.opis[i]
                }

                val slika =
                    bookElement.leafyBookElementImage

                bolest.slike?.let { slike ->
                    if(i < slike.size) {
                        if (!slike[i].isEmpty()) {

                            Glide.with(requireContext())
                                .load(slike[i])
                                .into(slika)

                        } else slika.visibility = View.GONE

                    } else slika.visibility = View.GONE

                } ?: run { slika.visibility = View.GONE }

                binding.bookScrollContainer.addView(bookElement.root)

            }

            if(bolest.preparati != null) {
                GlobalScope.launch {

                    val preparati = database.getDocuments<Preparat>(bolest.preparati, "preparati")

                    preparati.let { _preparati ->

                        if(_preparati.size > 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                createAdapterAndInflatePreparatiLayout(_preparati)
                            }

                        }

                    }

                }

            }

        }


    }

    /**
     * Kreira potreban layout za izlistavanje svih preparata za izabranu bolest
     * Kreira adapter i postavlja ga na recyclerview inflateovanog layouta
     *
     * @param preparati Preparati preuzeti iz baze koji pomažu u tretiranju ove bolesti
     * @see Preparat
     */
    fun createAdapterAndInflatePreparatiLayout(preparati: List<Preparat?>) {
        val preparatiElement =
            LeafyBookPreparatiElementBinding.inflate(layoutInflater, binding.bookScrollContainer, false)

        val preparatiAdapter = LeafyBookPreparatAdapter(preparati)
        preparatiAdapter.setOnClickListener(this)

        preparatiElement.leafyBookPreparatiRecycler.apply {
            this.adapter = preparatiAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.bookScrollContainer.addView(preparatiElement.root)
    }

    override fun onItemClicked(item: Any?) {
        item?.let {
            if(it is Preparat?)
                findNavController().navigate(BookViewerFragmentDirections.actionBookViewerFragmentToPreparatDetailsFragment(it))

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookViewerViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}