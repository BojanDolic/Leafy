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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.LeafyBookPreparatAdapter
import com.electroniccode.leafy.databinding.BookViewerFragmentBinding
import com.electroniccode.leafy.getDocuments
import com.electroniccode.leafy.models.Preparat
import com.electroniccode.leafy.viewmodels.BookViewerViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.lang.StringBuilder

class BookViewerFragment : Fragment() {

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

            for (i in bolest?.naslovi!!) {

                val index = bolest.naslovi.indexOf(i)

                val bookElement = layoutInflater.inflate(R.layout.leafy_book_element, binding.root, false)

                bookElement.findViewById<TextView>(R.id.leafy_book_element_title).text = bolest.naslovi.get(index)
                val opisElement = bookElement.findViewById<TextView>(R.id.leafy_book_element_desc)

                val stringBuilder = StringBuilder("")
                val stringovi: List<String> = bolest.opis?.get(index)?.split("\\n")!!



                if(stringovi.size > 0) {
                    for (string in stringovi) {
                        stringBuilder.append("\n" + string.trim())
                        opisElement.text = stringBuilder
                    }
                } else {
                    opisElement.text = bolest.opis.get(index)
                }

                //bookElement.findViewById<TextView>(R.id.leafy_book_element_desc).text = bolest.opis?.get(index)

                val slika = bookElement.findViewById<ImageView>(R.id.leafy_book_element_image)

                bolest.slike?.let {
                    if(index < it.size) {
                        if (!it[index].isEmpty()) {

                            Glide.with(requireContext())
                                .load(it.get(index))
                                .into(slika)

                        } else slika.visibility = View.GONE

                    } else slika.visibility = View.GONE

                } ?: run { slika.visibility = View.GONE }

                binding.bookScrollContainer.addView(bookElement)

            }

            if(bolest.preparati != null) {
                GlobalScope.launch {

                    val preparati = database.getDocuments<Preparat>(bolest.preparati, "preparati")

                    preparati.let {

                        if(it.size > 0) {

                            CoroutineScope(Dispatchers.Main).launch {
                                val preparatiElement = layoutInflater.inflate(R.layout.leafy_book_preparati_element, binding.root, false)
                                val preparatRecycler = preparatiElement.findViewById<RecyclerView>(R.id.leafy_book_preparati_recycler)

                                val preparatiAdapter = LeafyBookPreparatAdapter(it)

                                preparatRecycler.adapter = preparatiAdapter
                                preparatRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

                                binding.bookScrollContainer.addView(preparatiElement)
                            }

                        }

                    }

                }


                //Log.d("TAG", "onViewCreated: ${preparati}")

                /*database.document(bolest.preparati.get(0)).get().addOnSuccessListener {

                    val preparat = it.toObject(Preparat::class.java)

                    val preparati = listOf(preparat)
                    val preparatiAdapter = LeafyBookPreparatAdapter(preparati)

                    val preparatiElement = layoutInflater.inflate(R.layout.leafy_book_preparati_element, binding.root, false)

                    val preparatRecycler = preparatiElement.findViewById<RecyclerView>(R.id.leafy_book_preparati_recycler)
                    preparatRecycler.adapter = preparatiAdapter
                    preparatRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

                    binding.bookScrollContainer.addView(preparatiElement)

                }*/



            }

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