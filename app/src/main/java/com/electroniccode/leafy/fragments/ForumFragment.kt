package com.electroniccode.leafy.fragments

import android.animation.Animator
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.ForumPitanjaAdapter
import com.electroniccode.leafy.databinding.ForumFragmentBinding
import com.electroniccode.leafy.models.Komentar
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.viewmodels.ForumViewModel
import com.firebase.ui.firestore.SnapshotParser
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ForumFragment : Fragment(), ForumPitanjaAdapter.OnPitanjeClickListener {


    private lateinit var viewModel: ForumViewModel

    private var _binding: ForumFragmentBinding? = null
    private val binding get() = _binding!!

    val database = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //requireContext().theme.applyStyle(R.style.Theme_Leafy_HasStatus, true)

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
            .setQuery(forumQuery, pagedListConfig, object : SnapshotParser<Pitanje> {

                override fun parseSnapshot(snapshot: DocumentSnapshot): Pitanje {
                    val pitanje = snapshot.toObject(Pitanje::class.java)
                    pitanje?.idDokumenta = snapshot.id

                    Log.d("TAG", "parseSnapshot: " + snapshot.id + "\n\n" + pitanje?.idDokumenta)

                    return pitanje!!
                }
            })
            .build()

        val adapter = ForumPitanjaAdapter(options, binding.loadingProgressForum, database)
        adapter.setOnPitanjeClickListener(this)

        /*binding.forumRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.forumRecycler.adapter = adapter*/

        /**
         * Za animaciju tokom listanja recyclerview-a
         * Skriva fab tokom listanja
         */
        val scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when(newState) {

                    RecyclerView.SCROLL_STATE_IDLE, RecyclerView.SCROLL_STATE_SETTLING -> {

                        binding.kreirajPitanjeFab.visibility = View.VISIBLE
                            binding.kreirajPitanjeFab.animate().apply {
                                scaleX(1f)
                                scaleY(1f)
                                duration = 100

                                setListener(object : Animator.AnimatorListener {

                                    override fun onAnimationStart(animation: Animator?) {
                                    }

                                    override fun onAnimationCancel(animation: Animator?) {
                                    }

                                    override fun onAnimationRepeat(animation: Animator?) {
                                    }

                                    override fun onAnimationEnd(animation: Animator?) {
                                    }
                                })

                            }.start()
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        binding.kreirajPitanjeFab.animate().apply {
                            scaleX(0.1f)
                            scaleY(0.1f)
                            duration = 100

                            setListener(object : Animator.AnimatorListener {

                                override fun onAnimationStart(animation: Animator?) {
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    binding.kreirajPitanjeFab.visibility = View.GONE
                                }
                            })

                        }.start()
                    }


                }


            }
        }


        binding.forumRecycler.apply {

            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
            addOnScrollListener(scrollListener)

        }


        binding.kreirajPitanjeFab.setOnClickListener {
            findNavController().navigate(ForumFragmentDirections.actionForumFragmentToCreatePitanjeFragment())
        }



    }

    override fun onPitanjeClicked(snapshot: DocumentSnapshot?) {

        val pitanje = snapshot?.toObject(Pitanje::class.java)
        pitanje?.idDokumenta = snapshot?.id!!
        findNavController().navigate(ForumFragmentDirections.actionForumFragmentToPitanjeViewerFragment(pitanje))


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}