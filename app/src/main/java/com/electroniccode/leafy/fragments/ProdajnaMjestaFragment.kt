package com.electroniccode.leafy.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ProdajnaMjestaRecyclerAdapter
import com.electroniccode.leafy.databinding.FragmentProdajnaMjestaBinding
import com.electroniccode.leafy.deleteFirebaseDocument
import com.electroniccode.leafy.interfaces.OnDataChangedListener
import com.electroniccode.leafy.models.Mjesto
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Služi za izlistavanje svih prodajnih mjesta koje je korisnik dodao
 */

class ProdajnaMjestaFragment
    : Fragment(), OnDataChangedListener, ProdajnaMjestaRecyclerAdapter.OnProdajnoMjestoClickListener {

    private var _binding: FragmentProdajnaMjestaBinding? = null
    private val binding get() = _binding!!

    private val args: ProdajnaMjestaFragmentArgs by navArgs()

    private lateinit var adapter: ProdajnaMjestaRecyclerAdapter

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
            updateAdapter(it)
        }

        binding.prodajnoMjestoAddBtn.setOnClickListener {
            findNavController().navigate(ProdajnaMjestaFragmentDirections.actionProdajnaMjestaFragmentToCreateProdajnoMjestoFragment())
        }

    }

    fun updateAdapter(user: FirebaseUser) {
        val query = database.collection("korisnici/${user.uid}/prodajnaMjesta")

        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(9)
            .setPrefetchDistance(18)
            .build()

        val pagingOptions = FirestorePagingOptions.Builder<Mjesto>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(query, pagedListConfig, Mjesto::class.java)
            .build()

        val adapter = ProdajnaMjestaRecyclerAdapter(
            pagingOptions,
            binding.noMjestoWarningContainer,
            binding.shimmerLayout)
        this.adapter = adapter

        adapter.setOnProdajnoMjestoClickListener(this)
        adapter.setOnDataChangedListener(this)

        binding.prodajnaMjestaRecycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }


    }

    override fun onProdajnoMjestoClicked(doc: DocumentSnapshot?, position: Int) {

        doc?.let { documentSnapshot ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Obrisati prodajno mjesto ?")
                .setMessage(getString(R.string.delete_prodajno_mjesto_dialog_desc))
                .setPositiveButton("Obriši", DialogInterface.OnClickListener { dialog, which ->
                    deleteFirebaseDocument(doc.reference.path)
                    adapter.refresh()
                })
                .setNegativeButton("Odustani", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                .show()
        }

    }

    /**
     * Skriva dugme za kreiranje prodajnih mjesta
     */
    override fun hideNoDataPlaceHolder() {
        binding.prodajnoMjestoAddBtn.visibility = View.GONE
    }

    /**
     * Prikazuje dugme za kreiranje prodajnih mjesta
     */
    override fun showNoDataPlaceHolder() {
        binding.prodajnoMjestoAddBtn.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}