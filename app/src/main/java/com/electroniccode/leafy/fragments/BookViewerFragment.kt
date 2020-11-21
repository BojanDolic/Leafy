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
import com.electroniccode.leafy.models.Preparat
import com.electroniccode.leafy.viewmodels.BookViewerViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BookViewerFragment : Fragment() {

    companion object {
        fun newInstance() = BookViewerFragment()
    }

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
                bookElement.findViewById<TextView>(R.id.leafy_book_element_desc).text = bolest.opis?.get(index)

                val slika = bookElement.findViewById<ImageView>(R.id.leafy_book_element_image)

                Glide.with(requireContext())
                    .load(bolest.slikaBolesti)
                    .into(slika)

                binding.bookScrollContainer.addView(bookElement)

            }

            if(bolest.preparati != null) {

                Log.d("TAG", "onViewCreated: Postoji preparat")

                database.document(bolest.preparati.get(0)).get().addOnSuccessListener {

                    Log.d("TAG", "onViewCreated: Uspješno preuzet")

                    val preparat = it.toObject(Preparat::class.java)

                    val preparati = listOf(preparat)
                    val preparatiAdapter = LeafyBookPreparatAdapter(preparati)

                    val preparatiElement = layoutInflater.inflate(R.layout.leafy_book_preparati_element, binding.root, false)

                    val preparatRecycler = preparatiElement.findViewById<RecyclerView>(R.id.leafy_book_preparati_recycler)
                    preparatRecycler.adapter = preparatiAdapter
                    preparatRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

                    binding.bookScrollContainer.addView(preparatiElement)

                }






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