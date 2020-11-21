package com.electroniccode.leafy.viewmodels

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {

    var firebaseAuth: FirebaseAuth
    var db: FirebaseFirestore

    var isRegistering: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }


    fun registerUser(email: String, password: String, username: String) = registerUsingFirebaseAuth(email, password, username)

    private fun registerUsingFirebaseAuth(email: String, password: String, username: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun insertUserIntoDatabase(korisnik: HashMap<String, String>) = db.collection("korisnici").document(firebaseAuth.currentUser?.uid!!).set(korisnik)

    fun isEmailValid(email: String) = (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())

    fun isPasswordValid(password: String) = (!TextUtils.isEmpty(password) && password.length > 6)

    fun isUserNameValid(username: String) = (!TextUtils.isEmpty(username) && username.length > 4 && username.length < 20)

}