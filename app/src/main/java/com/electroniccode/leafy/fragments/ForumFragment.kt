package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ForumPitanjaAdapter
import com.electroniccode.leafy.databinding.ForumFragmentBinding
import com.electroniccode.leafy.models.Komentar
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.viewmodels.ForumViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ForumFragment : Fragment() {


    private lateinit var viewModel: ForumViewModel

    private var _binding: ForumFragmentBinding? = null
    private val binding get() = _binding!!

    val database = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ForumFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForumViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val forumQuery = database.collection("pitanja").orderBy("datum", Query.Direction.DESCENDING)

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(20)
            .build()

        val options = FirestorePagingOptions.Builder<Pitanje>()
            .setLifecycleOwner(this)
            .setQuery(forumQuery, pagedListConfig, Pitanje::class.java)
            .build()

        binding.forumRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.forumRecycler.adapter = ForumPitanjaAdapter(options)


        binding.kreirajPitanjeFab.setOnClickListener {
            findNavController().navigate(ForumFragmentDirections.actionForumFragmentToCreatePitanjeFragment())
        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}