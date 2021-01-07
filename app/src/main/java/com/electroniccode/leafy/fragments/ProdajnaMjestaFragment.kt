package com.electroniccode.leafy.fragments

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
import com.electroniccode.leafy.models.Mjesto
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Služi za izlistavanje svih prodajnih mjesta koje je korisnik dodao
 */

class ProdajnaMjestaFragment : Fragment(), ProdajnaMjestaRecyclerAdapter.OnProdajnoMjestoClickListener{

    private var _binding: FragmentProdajnaMjestaBinding? = null
    private val binding get() = _binding!!

    private val args: ProdajnaMjestaFragmentArgs by navArgs()

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
            binding.shimmerLayout).apply {

        }
        adapter.setOnProdajnoMjestoClickListener(this)

        binding.prodajnaMjestaRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }


    }

    override fun onProdajnoMjestoClicked(doc: DocumentSnapshot?) {

        if(args.enterType == 1) {
            val mjesto = doc?.toObject(Mjesto::class.java)
            //findNavController().navigate(ProdajnaMjestaFragmentDirections.actionProdajnaMjestaFragmentToProdajZitariceFragment(1, mjesto))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}