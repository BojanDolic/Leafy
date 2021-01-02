package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ForumPostsEditAdapter
import com.electroniccode.leafy.databinding.FragmentForumPostsBinding
import com.electroniccode.leafy.models.Pitanje
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ForumPostsFragment : Fragment(), ForumPostsEditAdapter.OnForumEditPostCardClick {

    private var _binding: FragmentForumPostsBinding? = null
    private val binding get() = _binding!!

    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForumPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user?.let {

            val query = FirebaseFirestore.getInstance()
                .collection("pitanja")
                .whereEqualTo("idAutora", it.uid)

            val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(20)
                .setPageSize(9)
                .build()

            val options = FirestorePagingOptions.Builder<Pitanje>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(query, pagedListConfig, Pitanje::class.java)
                .build()

            val adapter = ForumPostsEditAdapter(options)
            adapter.setOnForumEditPostCardClickedListener(this)
            binding.editForumPostsRecyclerview.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
            }


        }

    }

    /**
     * Poziva se kada korisnik napravi dugi klik na jednu od kartica pitanja
     * Pojavljuje se popup meni koji ima opciju da izbriše
     */
    override fun onForumPostLongClicked(dokument: DocumentSnapshot?) {

        dokument?.let {



        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}