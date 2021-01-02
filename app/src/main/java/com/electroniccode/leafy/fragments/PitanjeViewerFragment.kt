package com.electroniccode.leafy.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.adapters.PitanjeKomentariAdapter
import com.electroniccode.leafy.databinding.PitanjeViewerFragmentBinding
import com.electroniccode.leafy.models.Komentar
import com.electroniccode.leafy.models.Pitanje
import com.electroniccode.leafy.models.User
import com.electroniccode.leafy.viewmodels.PitanjeViewerViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class PitanjeViewerFragment : Fragment(), PitanjeKomentariAdapter.OnKomentarMenuItemClicked {


    val args: PitanjeViewerFragmentArgs by navArgs()

    val database = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance()

    private lateinit var viewModel: PitanjeViewerViewModel

    var _binding: PitanjeViewerFragmentBinding? = null
    val binding get() = _binding!!

    var pitanje: Pitanje? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = PitanjeViewerFragmentBinding.inflate(
            inflater,
            container,
            false
        )


        //requireActivity().setActionBar(binding.pitanjeViewerToolbar)
        NavigationUI.setupWithNavController(binding.pitanjeViewerToolbar, findNavController())

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pitanje = args.pitanjeObject


        pitanje?.let { pitanje ->

            binding.pitanjeTekstPitanja.text = pitanje.tekstPitanja
            binding.pitanjeOpisPitanja.text = pitanje.opis


            database.document("korisnici/${pitanje.idAutora}").get().addOnCompleteListener {

                if (it.isSuccessful) {

                    val user = it.result?.toObject(User::class.java)

                    user?.let {
                        binding.pitanjeImeAutoraText.text = user.korisnickoIme
                        Glide.with(requireContext())
                            .load(user.slikaKorisnika)
                            .transform(CircleCrop())
                            .error(R.drawable.no_profile_pic_placeholder)
                            .into(binding.pitanjeSlikaAutora)
                    } ?: run { binding.pitanjeImeAutoraText.text = "Nepoznato" }


                } else binding.pitanjeImeAutoraText.text = "Nepoznato"


            }

            loadComments(pitanje)


            if (pitanje.slikaPitanja == "")
                binding.pitanjeSlikaContainer.visibility = View.GONE
            else Glide.with(requireContext())
                .load(pitanje.slikaPitanja)
                .into(binding.pitanjeSlikaPitanja)


            binding.pitanjeKomentarDialogFab.setOnClickListener {
                showKomentarDialog()
            }


        }


        binding.pitanjeSlikaPitanja.setOnClickListener {

            findNavController().navigate(
                PitanjeViewerFragmentDirections.actionPitanjeViewerFragmentToPhotoViewerActivity(
                    pitanje?.slikaPitanja!!
                )
            )

            //binding.pitanjeSlikaPitanja.toggleFullScreen()
        }


    }

    fun showKomentarDialog() {

        val dialogInput = layoutInflater.inflate(R.layout.edittext_view, null)


        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Dodaj odgovor")
            setMessage("Upišite vaš odgovor u polje ispod")
            setView(dialogInput)
            setPositiveButton("Odgovori", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    val dialogEditText = dialogInput.findViewById<TextInputEditText>(R.id.dialog_edittext)

                    dialogEditText?.let {

                        val komentar = Komentar(tekstKomentara = it.text.toString(), idAutora = user.uid!!)

                        database.collection("pitanja/${pitanje?.idDokumenta}/komentari").add(komentar)
                            .addOnCompleteListener {

                                if (it.isSuccessful) {

                                    dialog?.dismiss()

                                    Snackbar.make(
                                        binding.root,
                                        "Komentar dodat !",
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    database.runTransaction {
                                        it.update(database.collection("korisnici").document(user.uid!!), "odgovora", FieldValue.increment(1))

                                    }


                                } else {
                                    dialog?.dismiss()

                                    Snackbar.make(
                                            binding.root,
                                            "Greška pri dodavanju komentara !",
                                            Snackbar.LENGTH_LONG
                                        )
                                        .setBackgroundTint(resources.getColor(R.color.errorRed))
                                        .show()
                                }


                            }

                    }

                }
            })
            setNegativeButton("Nazad", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }

            })
        }.show()

    }

    fun loadComments(pitanje: Pitanje) {

        val query = database
            .collection("pitanja/${pitanje.idDokumenta}/komentari")
            .orderBy("najboljiKomentar", Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<Komentar>()
            .setLifecycleOwner(this)
            .setQuery(query, object : SnapshotParser<Komentar> {

                override fun parseSnapshot(snapshot: DocumentSnapshot): Komentar {
                    val komentar = snapshot.toObject(Komentar::class.java)
                    komentar?.idKomentara = snapshot.id

                    return komentar!!
                }
            })
            .build()

        val adapter = PitanjeKomentariAdapter(
            options,
            database,
            binding.pitanjeKomentariProgressIndicator,
            binding.pitanjeKomentariRecycler,
            binding.pitanjeNoCommentContainer,
            binding.pitanjeOdgovoriText,
            pitanje
        )
        adapter.setOnKomentarMenuClickListener(this)

        binding.pitanjeKomentariRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setAdapter(adapter)
        }


    }

    /**
     * Označava izabrani komentar kao najbolji
     * Označiti komentar može samo autor pitanja
     */

    override fun onKomentarMarkedBest(komentarID: String) {

        // Prvo komentarima postavljamo polje "najboljiKomentar" na false
        // da se nebi desilo da su dva komentara najbolja
        database.collection("pitanja/${pitanje?.idDokumenta}/komentari").get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    for (document in it.result?.documents.orEmpty()) {
                        document.reference.update("najboljiKomentar", false).addOnCompleteListener {

                        }

                    }

                    // Nakon postavljenih vrijednosti, postavljamo odabrani komentar kao najbolji
                    database.document("pitanja/${pitanje?.idDokumenta}/komentari/${komentarID}")
                        .update("najboljiKomentar", true).addOnCompleteListener {

                            if (it.isSuccessful) {
                                Snackbar.make(
                                    binding.root,
                                    "Komentar je označen kao najbolji !",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                Snackbar.make(binding.root, "Greška !", Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                        }

                }


            }


    }

    override fun onKomentarDelete(komentarID: String) {
        database.document("pitanja/${pitanje?.idDokumenta}/komentari/${komentarID}").delete()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    Snackbar
                        .make(binding.root, "Komentar obrisan !", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.darkGreen))
                        .show()
                } else {
                    Snackbar
                        .make(
                            binding.root,
                            "Greška pri brisanju komentara !",
                            Snackbar.LENGTH_SHORT
                        )
                        .setBackgroundTint(resources.getColor(R.color.errorRed))
                        .show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}