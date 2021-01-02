package com.electroniccode.leafy.fragments

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.RegisterFragmentBinding
import com.electroniccode.leafy.models.User
import com.electroniccode.leafy.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }


    private var _binding: RegisterFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        return binding.root
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }*/


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(R.drawable.leafy_banner)
            .into(binding.leafyBannerImg)

        //region Login textview
        val spannableString = SpannableString("Već imate nalog ?\nKliknite ovde")
        val clickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }

        spannableString.setSpan(clickableSpan, 17, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.imateNalogTekst.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
        //endregion


        viewModel.isRegistering.observe(viewLifecycleOwner, Observer {

            if (it) {
                binding.registerAccountBtn.isEnabled = false
                binding.registerAccountBtn.text = resources.getText(R.string.registracija_text)
            } else {
                binding.registerAccountBtn.isEnabled = true
                binding.registerAccountBtn.text = resources.getText(R.string.registruj_se_text)
            }

        })


        binding.registerAccountBtn.setOnClickListener {

            binding.usernameInputBox.error = null
            binding.passwordInputBox.error = null
            binding.emailInputBox.error = null

            val email = binding.emailTextInputEditText.text.toString()
            val password = binding.passwordTextInputEditText.text.toString()
            val username = binding.usernameTextInputEditText.text.toString()

            /**
             * Provjera svih unesenih podataka i registracija korisnika
             */
            if (viewModel.isEmailValid(email)) {
                if (viewModel.isPasswordValid(password)) {
                    if (viewModel.isUserNameValid(username)) {

                        viewModel.isRegistering.value = true

                        viewModel.registerUser(email, password, username)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {

                                    var token: String = ""

                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                        token = task.result!!

                                        val korisnik = User("", username, "", token)

                                        viewModel.insertUserIntoDatabase(korisnik)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    findNavController().navigate(
                                                        RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                                                    )
                                                } else {
                                                    viewModel.isRegistering.value = false
                                                    Toast.makeText(
                                                        requireContext(),
                                                        it.exception.toString(),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                    }
                                } else {
                                    viewModel.isRegistering.value = false
                                    Toast.makeText(
                                        requireContext(),
                                        it.exception.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                    } else binding.usernameInputBox.error =
                        "Ime mora biti manje od 20 karaktera, a veće od 4"
                } else binding.passwordInputBox.error = "Lozinka mora biti duža od 6 karaktera"
            } else binding.emailInputBox.error = "Neispravan e-mail"


        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}