package com.electroniccode.leafy.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.ProfilFragmentBinding
import com.electroniccode.leafy.models.User
import com.electroniccode.leafy.util.UtilFunctions
import com.electroniccode.leafy.viewmodels.ProfilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ProfilFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var viewModel: ProfilViewModel

    private val database = FirebaseFirestore.getInstance()

    private val auth by lazy { FirebaseAuth.getInstance() }

    private val user by lazy { auth.currentUser }

    private var _binding: ProfilFragmentBinding? = null

    private var popupMenu: PopupMenu? = null

    private val binding get() = _binding!!

    var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ProfilViewModel::class.java)
        _binding = ProfilFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userListener = object : EventListener<DocumentSnapshot> {

            override fun onEvent(
                value: DocumentSnapshot?,
                error: FirebaseFirestoreException?
            ) {

                value?.let {
                    if (it.exists()) {

                        viewModel.user = it.toObject(User::class.java)
                        updateUserInfo(viewModel.user)

                    }

                }

            }
        }


        viewModel.user.let {
            user?.let { user ->

                GlobalScope.launch {

                    try {
                        val userResult = database.document("korisnici/${user.uid}").get().await()

                        userResult?.let {
                            if (it.exists()) {
                                viewModel.user = it.toObject(User::class.java)

                                withContext(Dispatchers.Main) {
                                    updateUserInfo(viewModel.user)
                                }
                            }
                        }

                        // Realtime update
                        listenerRegistration =
                            database.document("korisnici/${user.uid}")
                                .addSnapshotListener(userListener)

                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }


                }

            }
        } ?: run { updateUserInfo(viewModel.user) }



        createPopUpMenu()

        binding.profilOptionsBtn.setOnClickListener {
            popupMenu?.show()
        }

        binding.profilEditPostsCard.setOnClickListener {
            findNavController().navigate(ProfilFragmentDirections.actionProfilFragmentToEditPostsFragment())
        }

        binding.profilProdajnaMjestaCard.setOnClickListener {
            findNavController().navigate(ProfilFragmentDirections.actionProfilFragmentToProdajnaMjestaFragment())
        }


    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.profil_menu_logout -> {
                auth.signOut()
                findNavController().navigate(ProfilFragmentDirections.actionProfilFragmentToLoginFragment())
            }
            R.id.profil_menu_podesavanja -> {
                findNavController().navigate(ProfilFragmentDirections.actionProfilFragmentToProfileSettingsFragment())
            }

        }
        return true
    }

    fun createPopUpMenu() {

        popupMenu = PopupMenu(
            requireContext(),
            binding.profilOptionsBtn
        ).apply {
            setOnMenuItemClickListener(this@ProfilFragment)
        }

        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.profil_options_menu, popupMenu?.menu)

    }

    fun updateUserInfo(user: User?) {

        user?.let {

            binding.profilImeKorisnikaText.text = it.korisnickoIme
            binding.profilStatistikaOdgovora.text = it.odgovora.toString()
            binding.profilStatistikaNajboljihOdgovora.text = it.najboljihOdgovora.toString()

            binding.profilRankKorisnikaText.text = UtilFunctions.getRank(it.najboljihOdgovora)

            if(it.najboljihOdgovora >= 15)
                hideRankPercentage()
            else {
                binding.profilProgressIndicator.visibility = View.VISIBLE

                val progress = UtilFunctions.getPercentageToNextRank(it.najboljihOdgovora)

                binding.profilStatistikaProgress.text =
                    String.format(resources.getString(R.string.progress_placeholder_text), progress)
                binding.profilProgressIndicator.progress = progress
            }


            Glide.with(requireContext())
                .load(it.slikaKorisnika)
                .transform(CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.profile_icon_placeholder)
                .into(binding.profileIcon)
        }

    }

    fun hideRankPercentage() {
        binding.profilProgressIndicator.visibility = View.GONE
        binding.profilStatistikaProgress.text = "Dostigli ste najveći rank !"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listenerRegistration?.remove()
    }

}