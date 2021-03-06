package com.electroniccode.leafy.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.electroniccode.leafy.interfaces.OnDataChangedListener
import com.electroniccode.leafy.adapters.ForumPostsEditAdapter
import com.electroniccode.leafy.databinding.FragmentForumPostsBinding
import com.electroniccode.leafy.deleteFirebaseDocument
import com.electroniccode.leafy.models.Pitanje
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ForumPostsFragment : Fragment(), ForumPostsEditAdapter.OnForumEditPostCardClick,
    OnDataChangedListener {

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


            val options = FirestoreRecyclerOptions.Builder<Pitanje>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(query, Pitanje::class.java)
                .build()

            val adapter = ForumPostsEditAdapter(options)
            adapter.setOnForumEditPostCardClickedListener(this)
            adapter.setDataListener(this)
            binding.editForumPostsRecyclerview.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
            }


        }

    }

    /**
     * Poziva se kada korisnik napravi dugi klik na jednu od kartica pitanja
     * Pojavljuje se popup meni za brisanje pitanja
     */
    override fun onForumPostLongClicked(dokument: DocumentSnapshot?) {
        dokument?.let {
            showDeleteDialog(it)
        }
    }

    /**
     * Funkcija koja prikazuje dialog za brisanje pitanja
     *
     * @param document Dokument kojeg treba izbrisati
     *
     * @see com.electroniccode.leafy.deleteFirebaseDocument
     */
    fun showDeleteDialog(document: DocumentSnapshot) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Obrisati pitanje ?")
            .setMessage("Da li želite obrisati ovo pitanje ?\nAko obrišete pitanje više ga nećete moći vratiti.")
            .setPositiveButton("Obriši", DialogInterface.OnClickListener { dialog, which ->
                deleteFirebaseDocument(document.reference.path)
            })
            .setNegativeButton("Odustani", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            .show()
    }

    override fun hideNoDataPlaceHolder() {
        binding.noDataContainer.visibility = View.GONE
        binding.editForumPostsRecyclerview.visibility = View.VISIBLE
    }

    override fun showNoDataPlaceHolder() {
        binding.editForumPostsRecyclerview.visibility = View.GONE
        binding.noDataContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}