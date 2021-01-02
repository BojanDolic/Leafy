package com.electroniccode.leafy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.electroniccode.leafy.R
import com.electroniccode.leafy.databinding.FragmentStartScreenBinding
import com.electroniccode.leafy.util.FirebaseFCMService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.internal.IdTokenListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception

class StartScreenFragment : Fragment() {

    private var _binding: FragmentStartScreenBinding? = null

    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private val database = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartScreenBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.visibility = View.GONE
        binding.loginBtn.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser

        user?.let {

            it.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val token = task.result

                            database.document("korisnici/${user.uid}").update("token", token)
                            findNavController().navigate(StartScreenFragmentDirections.actionStartScreenFragmentToMainFragment())
                        } else {
                            binding.registerBtn.visibility = View.VISIBLE
                            binding.loginBtn.visibility = View.VISIBLE
                        }
                    }

                } else {
                    binding.registerBtn.visibility = View.VISIBLE
                    binding.loginBtn.visibility = View.VISIBLE
                }

            }
            /*it.getIdToken(true).addOnCompleteListener {
                if(it.isSuccessful) {
                    val token = it.result?.token

                    database.document("korisnici/${user.uid}").update("token", token)
                    findNavController().navigate(StartScreenFragmentDirections.actionStartScreenFragmentToMainFragment())
                } else {
                    binding.registerBtn.visibility = View.VISIBLE
                    binding.loginBtn.visibility = View.VISIBLE
                }
            }*/


        } ?: run {
            binding.registerBtn.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.VISIBLE
        }

        /*authStateListener = FirebaseAuth.AuthStateListener {
            if(it.currentUser != null)
                findNavController().navigate(StartScreenFragmentDirections.actionStartScreenFragmentToMainFragment())
            else {
                binding.registerBtn.visibility = View.VISIBLE
                binding.loginBtn.visibility = View.VISIBLE
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)*/




        binding.registerBtn.setOnClickListener {
            findNavController().navigate(StartScreenFragmentDirections.actionStartScreenFragmentToRegisterFragment())
        }

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(StartScreenFragmentDirections.actionStartScreenFragmentToLoginFragment())
        }

    }


    override fun onStop() {
        super.onStop()
        //firebaseAuth.removeAuthStateListener(authStateListener)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}