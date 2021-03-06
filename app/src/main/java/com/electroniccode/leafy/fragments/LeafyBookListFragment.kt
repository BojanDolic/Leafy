package com.electroniccode.leafy.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.electroniccode.leafy.adapters.BiljkeListAdapter
import com.electroniccode.leafy.adapters.BolestiRecyclerAdapter
import com.electroniccode.leafy.adapters.LeafyBookPreparatAdapter
import com.electroniccode.leafy.databinding.LeafyBookListFragmentBinding
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Biljka
import com.electroniccode.leafy.models.Bolest
import com.electroniccode.leafy.models.Preparat
import com.electroniccode.leafy.util.Constants
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.datatransport.runtime.scheduling.SchedulingConfigModule_ConfigFactory.config
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job


class LeafyBookListFragment
    : Fragment(), OnAdapterItemClickedListener {

    private var _binding: LeafyBookListFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: LeafyBookListFragmentArgs by navArgs()

    private var kategorija = ""
    private var kategorijaFormatted: List<String> = listOf()

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LeafyBookListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kategorija = getKategorija()

        if(kategorija.isNotEmpty()) {

            kategorijaFormatted = kategorija.split(" ")
            val query = FirebaseFirestore.getInstance().collection(kategorijaFormatted[0])

            setupPagingConfig(query)

        }

    }

    /**
     * Inicijalizuje potrebnu konfiguraciju za paged adapter i kreira adapter
     *
     * @param query query za preuzimanje podataka
     */
    fun setupPagingConfig(query: Query) {
        val pagedConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        if(TextUtils.equals(kategorija, Constants.KATEGORIJA_FUNGICIDI)
            || TextUtils.equals(kategorija, Constants.KATEGORIJA_HERBICID)) {
            getPreparate(query)
        }
        else {
            val options = getOptionsBasedOnKategorija(query, pagedConfig)

            val adapter = getAdapterBasedOnKategorija(options)

            if(TextUtils.equals(kategorija, Constants.KATEGORIJA_BILJKE))
                binding.bookListRecycler.apply {
                    layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    this.adapter = adapter
                }
            else binding.bookListRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = adapter
            }
        }

    }

    //region Za fungicide adapter
    fun getPreparate(query: Query) {
        query.whereEqualTo("tipPreparata", kategorijaFormatted[1]).get().addOnCompleteListener {
            if(it.isSuccessful)
                setupPreparatiAdapter(it.result.toObjects(Preparat::class.java))
        }.addOnFailureListener {
            Log.e("TAG", "getPreparate: ", it)
        }
    }

    fun setupPreparatiAdapter(preparati: List<Preparat>) {
        val adapter = LeafyBookPreparatAdapter(preparati)
        adapter.setOnClickListener(this)

        _binding?.let {
            binding.bookListRecycler.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

    }
    //endregion
    /**
     * Kreira potreban adapter na osnovu prethodno izabrane kategorije
     *
     * @param options opcije potrebne za adapter
     * @return Vraća odgovarajući adapter
     */
    fun getAdapterBasedOnKategorija(options: FirestorePagingOptions<*>): RecyclerView.Adapter<*> {
        if(TextUtils.equals(kategorija, Constants.KATEGORIJA_BILJKE))
            return BiljkeListAdapter(options as FirestorePagingOptions<Biljka>).apply {
                setOnClickListener(this@LeafyBookListFragment)
            }
        else if(TextUtils.equals(kategorija, Constants.KATEGORIJA_BOLESTI))
            return BolestiRecyclerAdapter(options as FirestorePagingOptions<Bolest>).apply {
                setOnClickListener(this@LeafyBookListFragment)
            }

        return BiljkeListAdapter(options as FirestorePagingOptions<Biljka>).apply {
            setOnClickListener(this@LeafyBookListFragment)
        }
    }


    /**
     * Kreira opcije potrebne za adapter na osnovu izabrane kategorije
     *
     * @param query query za preuzimanje podataka
     * @param pagedConfig konfiguracija za paged adapter
     *
     * @return Vraća odgovarajuće opcije na osnovu kategorije
     *
     * @see kategorija
     */
    fun getOptionsBasedOnKategorija(query: Query, pagedConfig: PagedList.Config): FirestorePagingOptions<*> {
        if(TextUtils.equals(kategorija, Constants.KATEGORIJA_BILJKE))
            return FirestorePagingOptions.Builder<Biljka>()
                .setLifecycleOwner(this)
                .setQuery(query, pagedConfig, Biljka::class.java)
                .build()
        else if(TextUtils.equals(kategorija, Constants.KATEGORIJA_FUNGICIDI))
            return FirestorePagingOptions.Builder<Biljka>()
                .setLifecycleOwner(this)
                .setQuery(query, pagedConfig, Biljka::class.java)
                .build()
        else if(TextUtils.equals(kategorija, Constants.KATEGORIJA_BOLESTI))
            return FirestorePagingOptions.Builder<Bolest>()
                .setLifecycleOwner(this)
                .setQuery(query, pagedConfig, Bolest::class.java)
                .build()

        return FirestorePagingOptions.Builder<Biljka>()
            .setLifecycleOwner(this)
            .setQuery(query, pagedConfig, Biljka::class.java)
            .build()
    }

    /**
     * Getter za kategoriju koja je izabrana u prethodnom fragmentu
     * @see LeafyBookFragment
     * @see Constants
     *
     * @return Vraća izabranu kategoriju
     */
    fun getKategorija(): String = args.kategorija

    override fun onItemClicked(item: Any?) {
        if(item is Preparat?) {
            findNavController().navigate(
                LeafyBookListFragmentDirections.actionLeafyBookListFragmentToPreparatDetailsFragment(
                    item
                )
            )
        }
        else if(item is Bolest) {
            findNavController().navigate(
                LeafyBookListFragmentDirections.actionLeafyBookListFragmentToBookViewerFragment(
                    item
                )
            )
        }
        else if(item is Biljka) {
            findNavController().navigate(
                LeafyBookListFragmentDirections.actionLeafyBookListFragmentToBiljkaDetailsFragment(
                    item
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}