package com.electroniccode.leafy.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.interfaces.OnDataChangedListener
import com.electroniccode.leafy.adapters.ProizvodiPostsEditAdapter
import com.electroniccode.leafy.databinding.ProizvodiPostsFragmentBinding
import com.electroniccode.leafy.deleteFirebaseDocument
import com.electroniccode.leafy.interfaces.OnAdapterItemClickedListener
import com.electroniccode.leafy.models.Proizvod
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProizvodiPostsFragment
    : Fragment(), OnDataChangedListener, OnAdapterItemClickedListener {

    private var _binding: ProizvodiPostsFragmentBinding? = null
    private val binding get() = _binding!!

    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProizvodiPostsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user?.let {
            val query = FirebaseFirestore.getInstance()
                .collection("proizvodi")
                .whereEqualTo("autorID", it.uid)


            val options = FirestoreRecyclerOptions.Builder<Proizvod>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(query, Proizvod::class.java)
                .build()

            val adapter = ProizvodiPostsEditAdapter(options)
            adapter.setOnItemClickedListener(this)
            adapter.setOnDataChangedListener(this)

            binding.proizvodiEditRecycler.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
            }

        }
    }

    /**
     * Poziva se kada korisnik ostvari dugi click sa jednim od cardova
     *
     * @param dokument Dokument kojeg treba izbrisati
     */
    override fun onItemLongClicked(dokument: DocumentSnapshot?) {

        dokument?.let {
            showDeleteDialog(dokument)
        }
    }

    /**
     * Funkcija koja prikazuje dialog za brisanje proizvoda
     *
     * @param dokument Dokument kojeg treba izbrisati
     *
     * @see com.electroniccode.leafy.deleteFirebaseDocument
     */
    fun showDeleteDialog(dokument: DocumentSnapshot) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Obrisati proizvod ?")
            .setMessage("Da li želite obrisati ovaj proizvod ?\nAko obrišete proizvod više ga nećete moći vratiti, a ostali korisnici ga neće moći vidjeti pri pretrazi !")
            .setPositiveButton("Obriši", DialogInterface.OnClickListener { dialog, which ->
                deleteFirebaseDocument(dokument.reference.path)
            })
            .setNegativeButton("Odustani", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            .show()
    }

    /**
     * Skriva UI element koji obavještava korisnika da nema elemenata za prikaz
     */
    override fun hideNoDataPlaceHolder() {
        binding.noDataContainer.visibility = View.GONE
        binding.proizvodiEditRecycler.visibility = View.VISIBLE
    }

    /**
     * Prikazuje UI element koji obavještava korisnika da nema elemenata za prikaz
     */
    override fun showNoDataPlaceHolder() {
        binding.noDataContainer.visibility = View.VISIBLE
        binding.proizvodiEditRecycler.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}