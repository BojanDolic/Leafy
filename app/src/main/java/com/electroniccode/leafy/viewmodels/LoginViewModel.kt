package com.electroniccode.leafy.viewmodels

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    fun loginUser(email: String, password: String) = auth.signInWithEmailAndPassword(email, password)

    fun isEmailValid(email: String) = (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())

    fun isPasswordValid(password: String) = (!TextUtils.isEmpty(password) && password.length > 6)

}