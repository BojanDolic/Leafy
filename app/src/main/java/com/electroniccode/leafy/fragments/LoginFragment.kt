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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.LoginFragmentBinding
import com.electroniccode.leafy.viewmodels.LoginViewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    var _binding: LoginFragmentBinding? = null

    val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(R.drawable.leafy_banner)
            .into(binding.leafyBannerImg)


        val spannableString = SpannableString("Nemate kreiran nalog ?\nKliknite ovde")
        val clickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }

        spannableString.setSpan(clickableSpan, 23, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.nemateNalogTekst.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }

        //region Login

        binding.loginBtn.setOnClickListener {

            binding.passwordInputBox.error = null
            binding.emailInputBox.error = null

            val email = binding.emailTextInputEditText.text.toString()
            val password = binding.passwordTextInputEditText.text.toString()


            if(viewModel.isEmailValid(email)) {
                if(viewModel.isPasswordValid(password)) {

                    viewModel.loginUser(email, password).addOnCompleteListener {

                        if(it.isSuccessful)
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                        else Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()

                    }
                } else binding.passwordInputBox.error = "Lozinka mora biti duža od 6 karaktera"
            } else binding.emailInputBox.error = "Neispravan e-mail"

        }

        //endregion




    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}