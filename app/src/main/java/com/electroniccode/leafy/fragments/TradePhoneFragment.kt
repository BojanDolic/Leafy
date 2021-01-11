package com.electroniccode.leafy.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.TradePhoneFragmentBinding
import com.electroniccode.leafy.util.UtilFunctions
import com.electroniccode.leafy.viewmodels.LeafyTradeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.coroutines.coroutineContext


class TradePhoneFragment : Fragment() {


    private var _binding: TradePhoneFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by navGraphViewModels<LeafyTradeViewModel>(R.id.create_proizvod_graph)

    private val user by lazy { FirebaseAuth.getInstance().currentUser }
    private val database by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TradePhoneFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(binding.sellCropToolbar, findNavController())

        binding.tradePhoneKoristenjeBtn.setOnClickListener {
            showKoristenjeDialog()
        }

        binding.tradePhoneInput.addTextChangedListener {
            viewModel.brojTelefona = it.toString()
        }

        viewModel.isPhoneUsageAccepted().observe(viewLifecycleOwner, {
            if(it) {
                if(viewModel.isBrojTelefonaValid())
                    binding.tradeKreirajProizvodBtn.visibility = View.VISIBLE
            } else {
                if(binding.tradeKreirajProizvodBtn.visibility == View.VISIBLE)
                    binding.tradeKreirajProizvodBtn.visibility = View.GONE
            }
        })

        binding.tradeKreirajProizvodBtn.setOnClickListener {

            user?.let {
                val proizvod = viewModel.kreirajProizvod(it)

                Snackbar.make(binding.root, "Kreiranje proizvoda...", Snackbar.LENGTH_SHORT).show()
                binding.tradeKreirajProizvodBtn.isEnabled = false

                GlobalScope.launch {

                    try {

                        val file = UtilFunctions.compressImageToFile(requireContext(), viewModel.slikaProizvodaUri.value!!).await()

                        val photoRef = storage.reference.child("slikeOglasa/${UUID.randomUUID()}")
                        photoRef.putFile(file.toUri()).await()
                        val downloadUrl = photoRef.downloadUrl.await()

                        proizvod.slikaProizvoda = downloadUrl.toString()

                        database.collection("proizvodi").add(proizvod).await()

                        withContext(Dispatchers.Main) {

                            Snackbar.make(binding.root, "Proizvod uspješno kreiran", Snackbar.LENGTH_SHORT).apply {
                                setBackgroundTint(resources.getColor(R.color.darkGreen, null))
                            }.show()

                            object : CountDownTimer(1500, 1000) {
                                override fun onTick(millisUntilFinished: Long) {

                                }

                                override fun onFinish() {
                                    findNavController().navigate(TradePhoneFragmentDirections.actionGlobalMainFragment())
                                }
                            }.start()

                            binding.tradeKreirajProizvodBtn.isEnabled = true
                        }

                    } catch (e: Throwable) {
                        Log.e("TAG", "onViewCreated: ", e)
                    }

                }

            }

        }

    }

    fun showKoristenjeDialog() {

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.trade_phone_koristenje_dialog_title))
            .setMessage(resources.getString(R.string.trade_phone_koristenje_dialog_text))
            .setPositiveButton("Prihvatam") { dialog, which ->
                GlobalScope.launch {
                    try {
                        viewModel.dataStore.writePhoneUsageValue(true)
                    } catch (e: Throwable) {
                        Log.e("TAG", "showKoristenjeDialog: ", e)
                    }
                }
                if(viewModel.isBrojTelefonaValid())
                    binding.tradeKreirajProizvodBtn.visibility = View.VISIBLE
            }
            .setNegativeButton("Odbijam") { dialog, which ->
                GlobalScope.launch {
                    try {
                        viewModel.dataStore.writePhoneUsageValue(false)
                    } catch (e: Throwable) {
                        Log.e("TAG", "showKoristenjeDialog: ", e)
                    }
                }
                binding.tradeKreirajProizvodBtn.visibility = View.VISIBLE
            }
            .setCancelable(false)

        dialogBuilder.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}