package com.electroniccode.leafy.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.ProfileSettingsFragmentBinding
import com.electroniccode.leafy.viewmodels.ProfileSettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import java.util.*


class ProfileSettingsFragment : Fragment() {


    private lateinit var viewModel: ProfileSettingsViewModel

    private var _binding: ProfileSettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private val userAuth by lazy { FirebaseAuth.getInstance().currentUser }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileSettingsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileSettingsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {

            userAuth?.let {
                viewModel.user = viewModel.getUserFromDatabase(it.uid)

                withContext(Dispatchers.Main) {

                    viewModel.user?.let { user ->

                        binding.profileSettingsUsername.setText(user.korisnickoIme)

                        viewModel.username = user.korisnickoIme

                        Glide.with(requireContext())
                            .load(user.slikaKorisnika)
                            .transform(CircleCrop())
                            .placeholder(R.drawable.profile_icon_placeholder)
                            .into(binding.profileSettingsIcon)
                    }

                }

            }


        }

        viewModel.isUpdating.observe(viewLifecycleOwner, { updating ->
            if (updating) {
                binding.profileSettingsSaveBtn.text = getString(R.string.cuvanje_podataka_text)
                binding.profileSettingsSaveBtn.isEnabled = false
            } else {
                binding.profileSettingsSaveBtn.text =
                    resources.getText(R.string.sacuvaj_izmjene_text)
                binding.profileSettingsSaveBtn.isEnabled = true
            }
        })


        binding.profileSettingsIconCard.setOnClickListener {
            selectImageFromGallery()
        }

        binding.profileSettingsUsernameCard.setOnClickListener {
            changeUserName()
        }

        binding.profileSettingsSaveBtn.setOnClickListener {
            updateUserSettings()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode != Activity.RESULT_CANCELED && data != null) {

                viewModel.profileImageUri = data.data

                Glide.with(requireContext())
                    .load(viewModel.profileImageUri)
                    .transform(CircleCrop())
                    .into(binding.profileSettingsIcon)

                binding.profileSettingsSaveBtn.visibility = View.VISIBLE

            }

        }

    }

    fun updateUserSettings() {
        userAuth?.let {
            viewModel.profileImageUri?.let { uri ->

                GlobalScope.launch {
                    withContext(Dispatchers.Default) {
                        viewModel.updateUserProfilePic(uri, it.uid)
                    }
                }
            }

            binding.profileSettingsUsername.isEnabled = false
            viewModel.updateUserProfileName(binding.profileSettingsUsername.text.toString(), it.uid)

        }


    }

    fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)

    }


    fun changeUserName() {

        binding.profileSettingsUsername.isEnabled = true
        binding.profileSettingsUsername.requestFocus()
        binding.profileSettingsUsername.setSelection(binding.profileSettingsUsername.text.length)

        val imm: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        binding.profileSettingsSaveBtn.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}